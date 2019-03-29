package com.paxing.test.kaoqin.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

/**
 * ftp util
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/29
 */
public class FtpUtil {
    private String url;
    private int port;
    private String userName;
    private String password;

    /**
     * 上传文件
     *
     * @param destDirectory
     * @param srcFilePath
     * @throws IOException
     */
    public void upload(String destDirectory, String srcFilePath) throws IOException {
        FTPClient client = connect();
        try {
            uploadFile(client, destDirectory, srcFilePath);
        } finally {
            close(client);
        }
    }

    public void uploadFile(FTPClient client, String destDirectory, String srcFilePath) throws IOException {
        client.changeWorkingDirectory(destDirectory);
        File file = new File(srcFilePath);
        try (InputStream inputStream = new FileInputStream(file)) {
            client.storeFile(destDirectory + file.getName(), inputStream);
        }
    }

    /**
     * 下载文件
     *
     * @param localPath
     * @param remotePath
     * @param fileName
     * @return
     * @throws IOException
     */
    public File download(String localPath, String remotePath, String fileName) throws IOException {
        FTPClient client = connect();
        try {
            if (!existsLocalPath(localPath)) {
                throw new IOException("本地目录：" + localPath + " 创建失败");
            }
            String localFile = localPath + File.separator + fileName;
            File file = new File(localFile);
            if (!file.createNewFile()) {
                throw new IOException("本地文件：" + localFile + " 创建失败");
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                if (!(client.changeWorkingDirectory(remotePath) && client.retrieveFile(fileName, fos))) {
                    throw new IOException("文件：" + fileName + " 传输失败");
                }
            }
            return file;
        } finally {
            close(client);
        }

    }

    private boolean existsLocalPath(String localPath) {
        File file = new File(localPath);
        return file.exists() && file.isDirectory() || file.mkdir();
    }

    /**
     * 建立客户端连接
     *
     * @return
     * @throws IOException
     */
    private FTPClient connect() throws IOException {
        FTPClient client = new FTPClient();
        client.connect(url, port);
        if (StringUtils.isBlank(userName)) {
            client.login("anonymous", "anonymous");
        } else {
            client.login(userName, password);
        }
        client.enterLocalPassiveMode();
        client.setControlKeepAliveTimeout(20000L);
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.enterLocalActiveMode();
        client.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
        return client;
    }

    private void close(FTPClient client) throws IOException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }

    }
}
