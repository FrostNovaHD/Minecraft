package net.minecraft.server;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadWatchdog implements Runnable {
    private static final Logger a = LogManager.getLogger();
    private final DedicatedServer b;
    private final long c;

    public ThreadWatchdog(DedicatedServer dedicatedserver) {
        this.b = dedicatedserver;
        this.c = dedicatedserver.aY();
    }

    public void run() {
        while(this.b.isRunning()) {
            long i = this.b.ax();
            long j = SystemUtils.b();
            long k = j - i;
            if (k > this.c) {
                a.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float)k / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
                a.fatal("Considering it to be crashed, server will forcibly shutdown.");
                ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
                ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
                StringBuilder stringbuilder = new StringBuilder();
                Error error = new Error();

                for(ThreadInfo threadinfo : athreadinfo) {
                    if (threadinfo.getThreadId() == this.b.ay().getId()) {
                        error.setStackTrace(threadinfo.getStackTrace());
                    }

                    stringbuilder.append(threadinfo);
                    stringbuilder.append("\n");
                }

                CrashReport crashreport = new CrashReport("Watching Server", error);
                this.b.b(crashreport);
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Thread Dump");
                crashreportsystemdetails.a("Threads", stringbuilder);
                File file1 = new File(new File(this.b.s(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
                if (crashreport.a(file1)) {
                    a.error("This crash report has been saved to: {}", file1.getAbsolutePath());
                } else {
                    a.error("We were unable to save this crash report to disk.");
                }

                this.a();
            }

            try {
                Thread.sleep(i + this.c - j);
            } catch (InterruptedException var15) {
                ;
            }
        }

    }

    private void a() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    Runtime.getRuntime().halt(1);
                }
            }, 10000L);
            System.exit(1);
        } catch (Throwable var2) {
            Runtime.getRuntime().halt(1);
        }

    }
}