package edu.xidian.logtofile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * <pre>
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
 * </pre>
 */
public class LogcatHelper {
    static final String TAG = "LogcatHelper";
	private static LogcatHelper INSTANCE = null;
	private String mLogDir = null;   // 在sd卡中或本应用程序目录中，本日志文件存放的目录
	private String mLogPath = null;  // 日志文件全路径：目录 + File.separator + 日志文件名
	private LogDumper mLogDumper = null;

	/**
	 * 初始化目录
	 * @param context
	 * @param dir 在sd卡中或本应用程序目录中，本日志文件存放的目录
	 * @param logFname 日志文件名
	 */
	private void init(Context context,String dir,String logFname) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 优先保存到SD卡中
			mLogDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + dir;
		} else { // 如果SD卡不存在，就保存到本应用的目录下
			mLogDir = context.getFilesDir().getAbsolutePath()
					+ File.separator + dir;	
		}
		File file = new File(mLogDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		mLogPath = mLogDir + File.separator + logFname;
		
		Log.d(TAG,"Logfile is "+mLogPath);
	}

	/**
	 * 生成日志文件唯一实例
	 * @param context
	 * @param dir 在sd卡中或本应用程序目录中，本日志文件存放的目录
	 * @param logFname 日志文件名
	 */
	public static LogcatHelper getInstance(Context context,String dir,String logFname) {
		if (INSTANCE == null) {
			INSTANCE = new LogcatHelper(context,dir,logFname);
		}
		return INSTANCE;
	}

	private LogcatHelper(Context context,String dir,String logFname) {
		init(context,dir,logFname);
	}
		
	/**
	 * 开始记录日志
	 * @param format 设置记录特定tag，指定级别以上的日志："tag:priority tag:priority... *:S"
	 * @see cn.edu.xidian.util.LogcatHelper.LogDumper#set_logformat(String format)
	 */
	public void start(String format) {
		// 首先检查文件目录是否存在，有可能被删除掉了。
		File file = new File(mLogDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		if (mLogDumper == null) {
		  mLogDumper = new LogDumper(mLogPath,format);
		}
	    mLogDumper.start();
	}

	/**
	 * 终止记录日志
	 */
	public void stop() {
		if (mLogDumper != null) {
		    mLogDumper.stopLogcat();
		    
		    // 必须有，使得下次start()时，重新产生LogDumper对象，否则会有错误：Thread already started
		    mLogDumper = null;   
		}
	}
	
	/** 删除日志文件 */
	public boolean delLogfile() {
		File file = new File(mLogPath);
		if (file.delete()) return true;
		else return false;
	}
	
	/** 删除日志目录及文件，如果日志在SD卡根目录，直接删除日志文件，不删除SD卡根目录 */
	public void delLogDir() {
		File file = new File(mLogDir);
		
		// 删除日志目录下的所有目录和文件
		delfile(file);
		
		// 是SD卡根目录，不删除该目录。
		String sd_root = Environment.getExternalStorageDirectory().getAbsolutePath();
		String sd_root1 = sd_root + File.separator;
		if ((sd_root.compareToIgnoreCase(file.getAbsolutePath()) == 0) || 
		   (sd_root1.compareToIgnoreCase(file.getAbsolutePath()) == 0)) {
					return;
		}
		// 删除日志空目录
		file.delete();
	}
	
	/** 删除该目录下的所有目录和文件，不删除SD卡根目录**/
	void delfile(File file) {
		// /storage/sdcard0
	    String sd_root = Environment.getExternalStorageDirectory().getAbsolutePath();
		String sd_root1 = sd_root + File.separator;
		Log.d(TAG,"sd root:"+sd_root);
		// Log.d(TAG,"sd root1:"+sd_root1);
		// 是SD卡根目录，直接删除日志文件，不删除目录。
		if ((sd_root.compareToIgnoreCase(file.getAbsolutePath()) == 0) || 
		   (sd_root1.compareToIgnoreCase(file.getAbsolutePath()) == 0)) {
			delLogfile();
			Log.d(TAG,"delfile1:"+file.getAbsolutePath());
			return;
		}
		
		// 处理非SD卡根目录
		File[] files=file.listFiles();
		
		// 直接空目录，删除之。
		if (files.length == 0) { 
			if (file.delete()) {
				Log.d(TAG,"ok1,delete file:"+file.getAbsolutePath());
			}
			else {
				Log.d(TAG,"Error,delete file:"+file.getAbsolutePath());
			}
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
			  if (files[i].delete()) {
				  Log.d(TAG,"ok2,delete file:"+file.getAbsolutePath());
			  }
			  else {
				  Log.d(TAG,"Error,delete file:"+file.getAbsolutePath());
			  }
			}
			else {
				delfile(files[i]);
			}	
		}
	}
	
	/**
	 * LogDumper线程类
	 */
	private class LogDumper extends Thread {
		private Process m_logcatProc = null;
		private String m_logpath;
		private ArrayList<String> m_cmds = new ArrayList<String>();
		/**
		 * @param LogPath 日志文件全路径
		 * @param format 设置记录特定tag，指定级别以上的日志："tag:priority tag:priority... *:S"
		 */
		public LogDumper(String LogPath,String format) {
			m_logpath=LogPath;
			// 设置默认格式,记录所有日志
			set_logformat(format);
		}
		
		/**
		 * 设置拟制符
		 * 例如：<br>
		 *   set_logformat("demo_dis:D *:S"); //记录D以上的所有tag(demo_dis)的日志，最后的*:S特别有用，即除了前面明显指定的拟制符外，其它不输出<br>
		 *   set_logformat("dis_demo:D LogcatHelper:V *:S"); // dis_demo的D级别及以上，LogcatHelper全部，其它不打印 <br>
		 * 记录特定tag，指定级别以上的日志："tag:priority tag:priority..."
		 * 	  其中：
		 *   tag是Log系列函数中使用的TAG，如：Log.d(TAG,"日志信息");<br>
		 *   priority=V | D | I | W | E | F | S  <br>
		 *   分别对应Log.v(),Log.d(),Log.i(),Log.w(),Log.e(),Log.f() <br>
		 *   ordered from lowest to highest priority: <br>
		 *   • V — Verbose (lowest priority)
		 *   • D — Debug
		 *   • I — Info
		 *   • W — Warning
		 *   • E — Error
		 *   • F — Fatal
		 *   • S — Silent (highest priority, on which nothing is ever printed)
		 * @param format 为""时,记录所有日志
		 */
		private void set_logformat(String format) {
			m_cmds.clear();
			if (format.length() > 0) {
				m_cmds.add("logcat"); 
			    m_cmds.add("-v"); m_cmds.add("time"); 
			    m_cmds.add("-v"); m_cmds.add("threadtime"); 
			    m_cmds.add("-f"); m_cmds.add(m_logpath); 
			    m_cmds.add(format); 
			}
			else { // 无拟制符格式
				m_cmds.add("logcat"); 
				m_cmds.add("-v"); m_cmds.add("time"); 
				m_cmds.add("-v"); m_cmds.add("threadtime"); 
				m_cmds.add("-f"); m_cmds.add(m_logpath); 
			}
		}
		
		@Override
        public void run() {
			String[] cmds=new String[m_cmds.size()];
			m_cmds.toArray(cmds);
			try {
				m_logcatProc=Runtime.getRuntime().exec(cmds);
				
				// 注意，不能使用exec(String prog),而应使用exec(String[] progArray) 
				// -v,-f选项各单独一项数组元素，拟制符单独一项
				// ok
				//m_logcatProc=Runtime.getRuntime().exec(new String[]{"logcat","-f","/sdcard/abc.log","demo_dis:D *:S"});
				
				// ok
				// m_logcatProc=Runtime.getRuntime().exec(new String[]{"logcat","-v", "time","-v", "threadtime","-f","/sdcard/abc.log","demo_dis:D *:S"});
					
				// error,不能生成文件
				//m_logcatProc=Runtime.getRuntime().exec(new String[]{"logcat -v time -v threadtime","-f","/sdcard/abc.log","demo_dis:D *:S"});
				
				// error,不生成文件
				//m_logcatProc=Runtime.getRuntime().exec(new String[]{"logcat","-v time","-v threadtime","-f","/sdcard/abc.log","demo_dis:D *:S"});
				
				// error, 能生成文件，但是"tag:priority *:S"失效。因此不能使用exec(String prog),而应使用exec(String[] progArray) 
				// m_logcatProc=Runtime.getRuntime().exec("logcat -f /sdcard/abc.log demo_dis:D *:S");
				
				// error 不能生成文件
				//m_logcatProc=Runtime.getRuntime().exec(new String[]{"logcat","demo_dis:D *:S","-f","/sdcard/abc.log"});
			} catch (IOException e) {
				Log.d(TAG,"[LogDumper] logcat exception："+e.toString());
			}
			
		}
		
		
		public void stopLogcat() {
			Process tmpProc = null;
			try {
				// 刷新整个日志,然后退出，以保证最后的日志能记录至文件
				tmpProc = Runtime.getRuntime().exec("logcat -c");
			} catch (IOException e) {
				Log.d(TAG,"[LogDumper] exception："+e.toString());
			}
			
			try {
				sleep(1000); // 等待1s
			} catch (InterruptedException e) {
				Log.d(TAG,"[LogDumper] sleep exception："+e.toString());
			}
			
			if (tmpProc != null) {
				tmpProc.destroy();
			}
			if (m_logcatProc != null) {
				m_logcatProc.destroy();
				m_logcatProc = null;
			}
		}
	}
}