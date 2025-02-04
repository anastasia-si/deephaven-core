# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: deephaven/proto/inputtable.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from pydeephaven.proto import ticket_pb2 as deephaven_dot_proto_dot_ticket__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='deephaven/proto/inputtable.proto',
  package='io.deephaven.proto.backplane.grpc',
  syntax='proto3',
  serialized_options=b'H\001P\001',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n deephaven/proto/inputtable.proto\x12!io.deephaven.proto.backplane.grpc\x1a\x1c\x64\x65\x65phaven/proto/ticket.proto\"\x92\x01\n\x0f\x41\x64\x64TableRequest\x12>\n\x0binput_table\x18\x01 \x01(\x0b\x32).io.deephaven.proto.backplane.grpc.Ticket\x12?\n\x0ctable_to_add\x18\x02 \x01(\x0b\x32).io.deephaven.proto.backplane.grpc.Ticket\"\x12\n\x10\x41\x64\x64TableResponse\"\x98\x01\n\x12\x44\x65leteTableRequest\x12>\n\x0binput_table\x18\x01 \x01(\x0b\x32).io.deephaven.proto.backplane.grpc.Ticket\x12\x42\n\x0ftable_to_remove\x18\x02 \x01(\x0b\x32).io.deephaven.proto.backplane.grpc.Ticket\"\x15\n\x13\x44\x65leteTableResponse2\xa6\x02\n\x11InputTableService\x12\x81\x01\n\x14\x41\x64\x64TableToInputTable\x12\x32.io.deephaven.proto.backplane.grpc.AddTableRequest\x1a\x33.io.deephaven.proto.backplane.grpc.AddTableResponse\"\x00\x12\x8c\x01\n\x19\x44\x65leteTableFromInputTable\x12\x35.io.deephaven.proto.backplane.grpc.DeleteTableRequest\x1a\x36.io.deephaven.proto.backplane.grpc.DeleteTableResponse\"\x00\x42\x04H\x01P\x01\x62\x06proto3'
  ,
  dependencies=[deephaven_dot_proto_dot_ticket__pb2.DESCRIPTOR,])




_ADDTABLEREQUEST = _descriptor.Descriptor(
  name='AddTableRequest',
  full_name='io.deephaven.proto.backplane.grpc.AddTableRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='input_table', full_name='io.deephaven.proto.backplane.grpc.AddTableRequest.input_table', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='table_to_add', full_name='io.deephaven.proto.backplane.grpc.AddTableRequest.table_to_add', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=102,
  serialized_end=248,
)


_ADDTABLERESPONSE = _descriptor.Descriptor(
  name='AddTableResponse',
  full_name='io.deephaven.proto.backplane.grpc.AddTableResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=250,
  serialized_end=268,
)


_DELETETABLEREQUEST = _descriptor.Descriptor(
  name='DeleteTableRequest',
  full_name='io.deephaven.proto.backplane.grpc.DeleteTableRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='input_table', full_name='io.deephaven.proto.backplane.grpc.DeleteTableRequest.input_table', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='table_to_remove', full_name='io.deephaven.proto.backplane.grpc.DeleteTableRequest.table_to_remove', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=271,
  serialized_end=423,
)


_DELETETABLERESPONSE = _descriptor.Descriptor(
  name='DeleteTableResponse',
  full_name='io.deephaven.proto.backplane.grpc.DeleteTableResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=425,
  serialized_end=446,
)

_ADDTABLEREQUEST.fields_by_name['input_table'].message_type = deephaven_dot_proto_dot_ticket__pb2._TICKET
_ADDTABLEREQUEST.fields_by_name['table_to_add'].message_type = deephaven_dot_proto_dot_ticket__pb2._TICKET
_DELETETABLEREQUEST.fields_by_name['input_table'].message_type = deephaven_dot_proto_dot_ticket__pb2._TICKET
_DELETETABLEREQUEST.fields_by_name['table_to_remove'].message_type = deephaven_dot_proto_dot_ticket__pb2._TICKET
DESCRIPTOR.message_types_by_name['AddTableRequest'] = _ADDTABLEREQUEST
DESCRIPTOR.message_types_by_name['AddTableResponse'] = _ADDTABLERESPONSE
DESCRIPTOR.message_types_by_name['DeleteTableRequest'] = _DELETETABLEREQUEST
DESCRIPTOR.message_types_by_name['DeleteTableResponse'] = _DELETETABLERESPONSE
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

AddTableRequest = _reflection.GeneratedProtocolMessageType('AddTableRequest', (_message.Message,), {
  'DESCRIPTOR' : _ADDTABLEREQUEST,
  '__module__' : 'pydeephaven.proto.inputtable_pb2'
  # @@protoc_insertion_point(class_scope:io.deephaven.proto.backplane.grpc.AddTableRequest)
  })
_sym_db.RegisterMessage(AddTableRequest)

AddTableResponse = _reflection.GeneratedProtocolMessageType('AddTableResponse', (_message.Message,), {
  'DESCRIPTOR' : _ADDTABLERESPONSE,
  '__module__' : 'pydeephaven.proto.inputtable_pb2'
  # @@protoc_insertion_point(class_scope:io.deephaven.proto.backplane.grpc.AddTableResponse)
  })
_sym_db.RegisterMessage(AddTableResponse)

DeleteTableRequest = _reflection.GeneratedProtocolMessageType('DeleteTableRequest', (_message.Message,), {
  'DESCRIPTOR' : _DELETETABLEREQUEST,
  '__module__' : 'pydeephaven.proto.inputtable_pb2'
  # @@protoc_insertion_point(class_scope:io.deephaven.proto.backplane.grpc.DeleteTableRequest)
  })
_sym_db.RegisterMessage(DeleteTableRequest)

DeleteTableResponse = _reflection.GeneratedProtocolMessageType('DeleteTableResponse', (_message.Message,), {
  'DESCRIPTOR' : _DELETETABLERESPONSE,
  '__module__' : 'pydeephaven.proto.inputtable_pb2'
  # @@protoc_insertion_point(class_scope:io.deephaven.proto.backplane.grpc.DeleteTableResponse)
  })
_sym_db.RegisterMessage(DeleteTableResponse)


DESCRIPTOR._options = None

_INPUTTABLESERVICE = _descriptor.ServiceDescriptor(
  name='InputTableService',
  full_name='io.deephaven.proto.backplane.grpc.InputTableService',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  create_key=_descriptor._internal_create_key,
  serialized_start=449,
  serialized_end=743,
  methods=[
  _descriptor.MethodDescriptor(
    name='AddTableToInputTable',
    full_name='io.deephaven.proto.backplane.grpc.InputTableService.AddTableToInputTable',
    index=0,
    containing_service=None,
    input_type=_ADDTABLEREQUEST,
    output_type=_ADDTABLERESPONSE,
    serialized_options=None,
    create_key=_descriptor._internal_create_key,
  ),
  _descriptor.MethodDescriptor(
    name='DeleteTableFromInputTable',
    full_name='io.deephaven.proto.backplane.grpc.InputTableService.DeleteTableFromInputTable',
    index=1,
    containing_service=None,
    input_type=_DELETETABLEREQUEST,
    output_type=_DELETETABLERESPONSE,
    serialized_options=None,
    create_key=_descriptor._internal_create_key,
  ),
])
_sym_db.RegisterServiceDescriptor(_INPUTTABLESERVICE)

DESCRIPTOR.services_by_name['InputTableService'] = _INPUTTABLESERVICE

# @@protoc_insertion_point(module_scope)
