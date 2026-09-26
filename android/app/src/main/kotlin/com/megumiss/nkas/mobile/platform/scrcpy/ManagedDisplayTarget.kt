package com.megumiss.nkas.mobile.platform.scrcpy

data class ManagedDisplayTarget(
    val displayId: Int, val identity: String, val socket: String,
    val deviceUid: String, val bootId: String,
) {
    init {
        require(displayId > 0 && identity.matches(Regex("[a-z0-9]{12}"))) { "虚拟屏幕标识无效" }
        require(socket.matches(Regex("nkas-vd-[a-f0-9]{32}"))) { "虚拟屏幕会话无效" }
        require(deviceUid.isNotBlank() && bootId.isNotBlank()) { "虚拟屏幕设备标识缺失" }
    }

    fun checkDevice(uid: String, boot: String) {
        check(uid.trim() == deviceUid && boot.trim() == bootId) { "控制设备与实例屏幕所属设备不一致" }
    }

    fun checkInfo(response: String) {
        val fields = Regex("(\\w+)=([^\\s]+)").findAll(response).associate { it.groupValues[1] to it.groupValues[2] }
        check(response.startsWith("OK ") && fields["id"] == displayId.toString()
            && fields["identity"] == identity && fields["socket"] == socket) { "虚拟屏幕已失效，请重新连接" }
    }
}
