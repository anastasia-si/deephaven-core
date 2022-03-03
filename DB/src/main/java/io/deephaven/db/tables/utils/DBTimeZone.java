/*
 * Copyright (c) 2016-2021 Deephaven Data Labs and Patent Pending
 */

package io.deephaven.db.tables.utils;

import org.joda.time.DateTimeZone;

import java.util.Arrays;
import java.util.List;

/**
 * Defines Deephaven-supported timezones, which may be used for PQ-scheduling and display purposes
 */
public enum DBTimeZone {
    /**
     * America/New_York
     */
    TZ_NY(DateTimeZone.forID("America/New_York")),
    /**
     * America/New_York
     */
    TZ_ET(DateTimeZone.forID("America/New_York")),
    /**
     * America/Chicago
     */
    TZ_MN(DateTimeZone.forID("America/Chicago")),
    /**
     * America/Chicago
     */
    TZ_CT(DateTimeZone.forID("America/Chicago")),
    /**
     * America/Denver
     */
    TZ_MT(DateTimeZone.forID("America/Denver")),
    /**
     * America/Los_Angeles
     */
    TZ_PT(DateTimeZone.forID("America/Los_Angeles")),
    /**
     * Pacific/Honolulu
     */
    TZ_HI(DateTimeZone.forID("Pacific/Honolulu")),
    /**
     * America/Sao_Paulo
     */
    TZ_BT(DateTimeZone.forID("America/Sao_Paulo")),
    /**
     * Asia/Seoul
     */
    TZ_KR(DateTimeZone.forID("Asia/Seoul")),
    /**
     * Asia/Hong_Kong
     */
    TZ_HK(DateTimeZone.forID("Asia/Hong_Kong")),
    /**
     * Asia/Tokyo
     */
    TZ_JP(DateTimeZone.forID("Asia/Tokyo")),
    /**
     * Canada/Atlantic
     */
    TZ_AT(DateTimeZone.forID("Canada/Atlantic")),
    /**
     * Canada/Newfoundland
     */
    TZ_NF(DateTimeZone.forID("Canada/Newfoundland")),
    /**
     * America/Anchorage
     */
    TZ_AL(DateTimeZone.forID("America/Anchorage")),
    /**
     * Asia/Kolkata
     */
    TZ_IN(DateTimeZone.forID("Asia/Kolkata")),
    /**
     * Europe/Berlin
     */
    TZ_CE(DateTimeZone.forID("Europe/Berlin")),
    /**
     * Asia/Singapore
     */
    TZ_SG(DateTimeZone.forID("Asia/Singapore")),
    /**
     * Europe/London
     */
    TZ_LON(DateTimeZone.forID("Europe/London")),
    /**
     * Europe/Moscow
     */
    TZ_MOS(DateTimeZone.forID("Europe/Moscow")),
    /**
     * Asia/Shanghai
     */
    TZ_SHG(DateTimeZone.forID("Asia/Shanghai")),
    /**
     * Europe/Zurich
     */
    TZ_CH(DateTimeZone.forID("Europe/Zurich")),
    /**
     * Europe/Amsterdam
     */
    TZ_NL(DateTimeZone.forID("Europe/Amsterdam")),
    /**
     * Asia/Taipei
     */
    TZ_TW(DateTimeZone.forID("Asia/Taipei")),
    /**
     * Australia/Sydney
     */
    TZ_SYD(DateTimeZone.forID("Australia/Sydney")),
    /**
     * UTC
     */
    TZ_UTC(DateTimeZone.UTC);

    /**
     * The default time zone for display purposes.
     */
    public static DBTimeZone TZ_DEFAULT = TZ_NY;

    private DateTimeZone timeZone;

    DBTimeZone(DateTimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the underlying Joda time zone for this DBTimeZone.
     *
     * @return the underlying Joda time zone.
     */
    public DateTimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Find the corresponding DBTimeZone for a given Joda DateTimeZone.
     *
     * @param dateTimeZone the time zone to search for
     *
     * @return the corresponding DBTimeZone, or null if none was found
     */
    public static DBTimeZone lookup(DateTimeZone dateTimeZone) {
        for (DBTimeZone zone : values()) {
            if (zone.getTimeZone().equals(dateTimeZone)) {
                return zone;
            }
        }
        return lookupByOffset(dateTimeZone);
    }

    private static DBTimeZone lookupByOffset(DateTimeZone dateTimeZone) {
        for (DBTimeZone zone : values()) {
            if (zone.getTimeZone().getOffset(System.currentTimeMillis()) == dateTimeZone
                    .getOffset(System.currentTimeMillis())) {
                return zone;
            }
        }
        return null;
    }

    /**
     * This method returns the same contents as {@link DBTimeZone#values()}, but ordered by geographic location / UTC
     * offset. If two elements exist within the same timezone, they are second-order-sorted by name
     *
     * @return An array of DBTimeZones ordered by UTC-offset
     */
    public static DBTimeZone[] valuesByOffset() {
        final List<DBTimeZone> allZones = Arrays.asList(values());
        final long now = System.currentTimeMillis();

        allZones.sort((t1, t2) -> {
            int ret = t2.getTimeZone().getOffset(now) - t1.getTimeZone().getOffset(now);
            if (ret != 0) {
                return ret;
            } else {
                ret = t1.getTimeZone().getID().compareTo(t2.getTimeZone().getID());
                return ret != 0 ? ret : t1.name().compareTo(t2.name());
            }
        });

        return allZones.toArray(new DBTimeZone[0]);
    }

    /**
     * Get the default time zone.
     *
     * @return the default {@link DBTimeZone}
     */
    public static io.deephaven.db.tables.utils.DBTimeZone getTzDefault() {
        return TZ_DEFAULT;
    }

    /**
     * Set the default time zone.
     * 
     * @param tzDefault the {@link DBTimeZone} to be used as the default.
     */
    public static void setTzDefault(io.deephaven.db.tables.utils.DBTimeZone tzDefault) {
        TZ_DEFAULT = tzDefault;
    }
}