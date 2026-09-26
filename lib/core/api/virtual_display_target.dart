class VirtualDisplayTarget {
  const VirtualDisplayTarget({
    required this.displayId,
    required this.identity,
    required this.socket,
    required this.sessionId,
    required this.deviceUid,
    required this.bootId,
    required this.serial,
  });

  final int displayId;
  final String identity;
  final String socket;
  final String sessionId;
  final String deviceUid;
  final String bootId;
  final String serial;

  factory VirtualDisplayTarget.fromJson(Map<String, dynamic> json) {
    final id = json['displayId'];
    String field(String key) {
      final value = json[key];
      if (value is! String || value.isEmpty) {
        throw FormatException('缺少虚拟屏幕字段 $key');
      }
      return value;
    }

    if (id is! int || id <= 0) throw const FormatException('虚拟屏幕 ID 无效');
    return VirtualDisplayTarget(
      displayId: id,
      identity: field('identity'),
      socket: field('socket'),
      sessionId: field('sessionId'),
      deviceUid: field('deviceUid'),
      bootId: field('bootId'),
      serial: field('serial'),
    );
  }
}
