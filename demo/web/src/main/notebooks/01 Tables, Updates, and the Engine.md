# The Deephaven Engine, Tables, and Updates

Deephaven empowers you to build analytics and applications for all sorts of data-driven use cases.

We hope you find it relevant for challenging, enterprise-grade needs, but let's start with the fun stuff.

You can quickly see streaming data in a UI and do table operations, interactively exploring data in real-time as it changes.

For example, you can listen to a Kafka stream of cryptocurrency trades sourced from their native exchanges (like the ones below, built using the [XChange library](https://github.com/knowm/XChange)).

```python
from deephaven import KafkaTools as kt

def get_trades_stream():
    return kt.consumeToTable(
        { 'bootstrap.servers' : 'demo-kafka.c.deephaven-oss.internal:9092',
          'schema.registry.url' : 'http://demo-kafka.c.deephaven-oss.internal:8081' },
        'io.deephaven.crypto.kafka.TradesTopic',
        key = kt.IGNORE,
        value = kt.avro('io.deephaven.crypto.kafka.TradesTopic-io.deephaven.crypto.Trade'),
        offsets=kt.ALL_PARTITIONS_SEEK_TO_END,
        table_type='append')

trades_stream = get_trades_stream()
```
\
\
\
To keep the most recent ticks within view, you could sort the table descending by timestamp. Alternatively, you can reverse the table.

```python
# Not doing this:
# t = t.sortDescending("Timestamp")

trades_stream = trades_stream.reverse()
```
\
\
\
You have likely observed Deephaven's **_1st_ Fundamental Concept_**:\
Tables and streams are a single abstraction. Event streams, feeds, [soon] CDC, and other dynamic data are simply represented as incremental updates to a table. ([This write-up](https://deephaven.io/core/docs/conceptual/table-update-model/) describe's the table update model fundamental to this design.)

You can readily see that your table grows as greater volumes of data are inherited from the Kafka feed.

```python
row_count = trades_stream.countBy("Tot_Rows")
```
\
\
\
The script above illuminates Deephaven's **_2nd Fundamental Concept_**:\
Data flows from one named table (`trades_stream` in this example) to its dependent (`row_count`). This is both easy to script and powerful to use. Computer scientists know this as a [directed acyclic graph](https://en.wikipedia.org/wiki/Directed_acyclic_graph).\
\
If you flip back and forth between `trades_stream` and `row_count` you'll see that both are continuing to update. (Or pull them side-by-side to one another.)
\
\
\
**Updating views** just magically happen.

As you might expect, a named table can have multiple dependencies.\
After you run the following command, you'll see that all three of your tables are now updating in lock-step.

```python
row_count_by_instrument = trades_stream.countBy("Tot_Rows", "Instrument")\
    .sortDescending("Tot_Rows")
```
\
\
\
USDT is a cryptocurrency pinned to the dollar.  Pretend you wnat to consider them one in the same.\
Below is one way to use a replace() method to swap one for the other.\
To learn more about updateView (or other selection or projection alternatives, refer to [the docs](https://deephaven.io/core/docs/conceptual/choose-select-view-update/).

```python
trades_stream_cleaner = trades_stream.updateView("Instrument = Instrument.replace(`USDT`, `USD`)")

row_count_by_instrument = trades_stream_cleaner.countBy("Tot_Rows", "Instrument")\
    .sortDescending("Tot_Rows")
```
\
\
\
Counts are informative, but often you'll be interested in other aggregations. The script below shows both how to [bin data by time](https://deephaven.io/core/docs/reference/cheat-sheets/datetime-cheat-sheet/#downsampling-temporal-data-via-time-binning) and to [do multiple aggregations](https://deephaven.io/core/docs/how-to-guides/combined-aggregations/).

```python
from deephaven import ComboAggregateFactory as caf
multi_agg = trades_stream_cleaner.updateView("TimeBin = upperBin(KafkaTimestamp, MINUTE)")\
    .by(caf.AggCombo(
        caf.AggCount("Trade_Count"),
        caf.AggSum("Total_Size = Size"),
        caf.AggAvg("Avg_Size = Size", "Avg_Price = Price"),
        caf.AggMin("Low_Price = Price"),
        caf.AggMax("High_Price = Price")),"TimeBin", "Instrument")\
    .sortDescending("TimeBin", "Trade_Count")\
    .formatColumnWhere("Instrument", "Instrument = `BTC/USD`", "CYAN")
```
\
\
\
Filtering streams is straightforward. One simply uses `where()` to impose a huge range of [match, conditional, and combination filters](https://deephaven.io/core/docs/how-to-guides/use-filters/).

```python
# Filter on a manually-set filter
multi_agg_btc = multi_agg.where("Instrument = `BTC/USD`")
multi_agg_eth = multi_agg.where("Instrument = `ETH/USD`")

# Filter on a programatically set criteria
top_instrument = multi_agg.head(1)

multi_agg_row_0 = multi_agg.whereIn(top_instrument, "Instrument")\
    .formatColumns("Total_Size = heatmap(Total_Size, 10, 300, MAGENTA, CYAN)")
```
\
\
\
[Joining streams](https://deephaven.io/core/docs/how-to-guides/joins-overview/) is one of Deephaven's superpowers . Deephaven supports high-performance joins that are both relational in nature ...

```python
join_eth_btc = multi_agg_eth.view("TimeBin", "Eth_Avg_Price = Avg_Price")\
    .naturalJoin(multi_agg_btc, "TimeBin", "Btc_Avg_Price = Avg_Price")\
    .updateView("Ratio_Avg_Prices = Btc_Avg_Price / Eth_Avg_Price")\
    .formatColumns("Eth_Avg_Price = Decimal(`#,###.00`)", "Btc_Avg_Price = Decimal(`#,###.00`)")
```
\
\
\
... or [time series joins](https://deephaven.io/core/docs/reference/table-operations/join/aj/), where two sets of data are correlated to one another based on timestamps. The code below shows the last trade price and size of BTC at the time of each ETH trade event.

```python
# Time series 'as-of' join that looks for the exact Eth_Time from the left table (eth_trades) in the
# KafkaTimestamp in the right table (btc_trades).
# If there is no exact nanosecond match, the record with KafkaTimestamp just preceding Eth_Time is used

eth_trades = trades_stream.where("Instrument = `ETH/USD`")
btc_trades = trades_stream.where("Instrument = `BTC/USD`")

time_series_join_eth_btc = eth_trades.view("Eth_Time = KafkaTimestamp", "Eth_Price = Price")\
    .aj(btc_trades, "Eth_Time = KafkaTimestamp", "Btc_Price = Price, Btc_Time = KafkaTimestamp")\
    .updateView("Ratio_Each_Trade = Btc_Price / Eth_Price")\
    .formatColumns("Eth_Price = Decimal(`#,###.00`)", "Btc_Price = Decimal(`#,###.00`)")
```
\
\
\
To explore using these and other methods identically on static and updating data, please check out [the next notebook](02%20Stream%20and%20Batch%20Together.md).

```python
print("Learn about Deephaven's lambda architecture next.")
```