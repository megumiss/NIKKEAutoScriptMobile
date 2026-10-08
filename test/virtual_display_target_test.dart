import 'dart:convert';
import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:nkas_mobile/core/api/api_client.dart';
import 'package:nkas_mobile/core/api/virtual_display_target.dart';

void main() {
  final target = <String, dynamic>{
    'enabled': true,
    'available': true,
    'displayId': 19,
    'identity': 'abc123def456',
    'socket': 'nkas-vd-session',
    'sessionId': 'session',
    'deviceUid': 'phone',
    'bootId': 'boot',
    'serial': '127.0.0.1:5555',
  };

  test(
    'loads backend target without defaulting to the primary display',
    () async {
      final client = ApiClient(
        client: MockClient((request) async {
          expect(request.url.path, '/api/example/virtual-display');
          return http.Response(jsonEncode(target), 200);
        }),
      );
      expect(
        (await client.fetchVirtualDisplay(
          'http://localhost:12271',
          'example',
        ))?.displayId,
        19,
      );
      expect(
        () => VirtualDisplayTarget.fromJson({...target, 'displayId': 0}),
        throwsFormatException,
      );
    },
  );

  test(
    'unavailable enabled display raises an error instead of falling back',
    () async {
      final client = ApiClient(
        client: MockClient(
          (_) async => http.Response(
            jsonEncode({
              'enabled': true,
              'available': false,
              'reason': 'device disconnected',
            }),
            200,
          ),
        ),
      );
      await expectLater(
        client.fetchVirtualDisplay('http://localhost:12271', 'example'),
        throwsA(isA<ApiException>()),
      );
    },
  );

  test('old backend virtual mode requires an update', () async {
    final client = ApiClient(
      client: MockClient(
        (request) async => request.url.path.endsWith('/config')
            ? http.Response(
                jsonEncode({
                  'Emulator': {
                    'PhysicalDevice': {'Enable': true, 'VirtualDisplay': true},
                  },
                }),
                200,
              )
            : http.Response('', 404),
      ),
    );
    await expectLater(
      client.fetchVirtualDisplay('http://localhost:12271', 'example'),
      throwsA(isA<ApiException>()),
    );
  });
}
