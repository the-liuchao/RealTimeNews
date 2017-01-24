package com.liuming.mylibrary;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * XUtils3.3.4
 * Created by hp on 2017/1/5.
 */

public abstract class XApplication extends Application {
    private static XApplication instance;

    private DbManager.DaoConfig mDaoConfig;
    private DbManager mDbManager;
    private Stack<Activity> acts;


    public static XApplication getInstance() {
        return instance;
    }

    /**
     * 本地数据库版本
     *
     * @return
     */
    protected abstract int getDbVersion();

    /**
     * 数据库更新
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    protected abstract void upgrade(DbManager db, int oldVersion, int newVersion);

    /**
     * 异常重启activity的class
     *
     * @return
     */
    protected abstract Class<?> resetClass();

    @Override
    public void onCreate() {
        super.onCreate();
        XCrashHandle.getInstance().init(this);
        acts = new Stack<>();
        x.Ext.init(this);
        initDataBase();
        instance = this;
    }

    private void initDataBase() {
        mDaoConfig = new DbManager.DaoConfig()
                .setDbName("xutils.db")                                             //数据库名称
//                .setDbDir(Environment.getDataDirectory())                           //不设置dbDir时, 默认存储在app的私有目录.
                .setDbVersion(getDbVersion())                                       //设置版本号
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();                 //开启WAL, 对写入加速提升巨大
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {           //数据库升级
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                        upgrade(db, oldVersion, newVersion);
                    }
                });
        mDbManager = x.getDb(mDaoConfig);
    }

    public DbManager getDbManager() {
        return mDbManager;
    }

    /**
     * 添加activity
     */
    public void addAct(Activity act) {
        acts.add(act);
    }

    /**
     * 移除activity
     */
    public void removeAct(Activity act) {
        if (acts.contains(act)) {
            acts.remove(act);
        }
    }

    /**
     * finish
     *
     * @param act
     */
    public void finish(Activity act) {
        removeAct(act);
        act.finish();
    }

    public void finishAll() {
        Iterator iterator = acts.listIterator();
        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();
            acts.remove(activity);
            activity.finish();
        }
    }

    public String getTopAct() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).getClassName();
        else
            return null;
    }

    public Activity getAct(Class claszz) {
        for (Activity act : acts) {
            if (act.getClass().getName().equals(claszz.getName()))
                return act;
        }
        return null;
    }
}
