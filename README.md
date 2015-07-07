# LogToFile
保存android日志至SD卡中的文件
/**
 * 保存日志至sd卡,dir="",logFname则在SD卡根目录
 * (1) 获取唯一实例：LogcatHelper loghelper = LogcatHelper.getInstance(Context context,String dir,String logFname)
 * (2) 开始记录日志：loghelper.start(format)
 *     参数：String format;设置日志拟制格式符
 *         format="tag:priority tag:priority...*:S"; // 设置记录特定tag，指定级别priority以上的日志：
 *         format="dis_demo:D LogcatHelper:V *:S";   // dis_demo的D级别及以上，LogcatHelper全部，其它不打印
 * (3) 停止记录：loghelper.stop()
 * 
 * 使用下列logcat命令：
 * (1) 记录所有日志(默认状态):
 *     logcat -v time -v threadtime -f 日志文件名  
 * (2) 记录指定级别以上的所有tag的日志：
 *     logcat -v time -v threadtime -f 日志文件名  *:priority
 * (3) 记录特定tag，指定级别以上的日志：
 *     logcat -v time -v threadtime -f 日志文件名 tag:priority tag:priority *:S
 *   其中：
 *   tag是Log系列函数中使用的TAG，如：Log.d(TAG,"日志信息");
 *   priority=V | D | I | W | E | F | S  
 *   分别对应Log.v(),Log.d(),Log.i(),Log.w(),Log.e(),Log.f()
 *   ordered from lowest to highest priority:
 *   • V — Verbose (lowest priority)
 *   • D — Debug
 *   • I — Info
 *   • W — Warning
 *   • E — Error
 *   • F — Fatal
 *   • S — Silent (highest priority, on which nothing is ever printed)
 * lennovo手机中写入的文件在adb或ddms中可见，用手机中的文件管家可见，win7中的不可见
 */
