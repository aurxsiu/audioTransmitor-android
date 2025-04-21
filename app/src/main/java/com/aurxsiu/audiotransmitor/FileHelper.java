package com.aurxsiu.audiotransmitor;

import android.app.Activity;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileHelper {
    public final static String IpLogFileName = "IpLog.aur";
    private final File IpLogFile;
    private volatile ArrayList<OptionData> IpLog = null;
    public FileHelper(Activity activity){
        IpLogFile = new File(activity.getFilesDir(),IpLogFileName);
        if(!IpLogFile.isFile()){
            try {
                IpLogFile.createNewFile();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
            initIpLog();
        }
    }

    public ArrayList<OptionData> getIpLog(){
        if(IpLog == null){
            try (FileInputStream fileInputStream = new FileInputStream(IpLogFile)) {
                String read = new String(fileInputStream.readAllBytes(),StandardCharsets.UTF_8);
                IpLog = JsonHelper.encode(new TypeReference<ArrayList<OptionData>>() {
                },read);
                return IpLog;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }else{
            return IpLog;
        }
    }

    public void addIpToLog(OptionData data){
        IpLog.add(0,data);

        while (IpLog.size()>6){
            IpLog.remove(6);
        }
        setIpLog(IpLog);
    }

    private void initIpLog(){
        setIpLog(new ArrayList<>());
    }

    private void setIpLog(ArrayList<OptionData> data){
        try(FileOutputStream fileOutputStream = new FileOutputStream(IpLogFile)){
            fileOutputStream.write(JsonHelper.decode(data).getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void setIpFirst(OptionData data){
        IpLog.remove(data);
        IpLog.add(0,data);
        setIpLog(IpLog);
    }
    public void replaceIpLog(OptionData oldData,OptionData newData){
        IpLog.set(IpLog.indexOf(oldData),newData);
        setIpLog(IpLog);
    }
}