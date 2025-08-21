package com.example.sftpapp;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.requestPermissions;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sftpapp.R;
import com.google.android.material.button.MaterialButton;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

public class BrowseActivity extends AppCompatActivity {
    // Download file to app-specific external storage (private to app)
    private boolean downloadToAppExternalStorage(String remotePath, String fileName) {
        runOnUiThread(() -> showDownloadProgressDialog(remotePath, fileName));
        return true;
    }

    // Show download progress dialog for single file
    private void showDownloadProgressDialog(String remotePath, String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_download_modern, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView title = dialogView.findViewById(R.id.downloadTitle);
        TextView fileNameView = dialogView.findViewById(R.id.downloadFileName);
        TextView percentView = dialogView.findViewById(R.id.downloadPercent);
        ProgressBar progressBar = dialogView.findViewById(R.id.downloadProgressBar);
        Button cancelButton = dialogView.findViewById(R.id.downloadCancelButton);

        title.setText("Downloading file...");
        fileNameView.setText(fileName);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        final boolean[] cancelled = {false};
        cancelButton.setOnClickListener(v -> {
            cancelled[0] = true;
            dialog.dismiss();
        });

        new Thread(() -> {
            Session localSession = null;
            ChannelSftp localSftpChannel = null;
            try {
                // Create a new SFTP session/channel for this download
                JSch jsch = new JSch();
                localSession = jsch.getSession(username, host, port);
                localSession.setPassword(password);
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                config.put("ServerAliveInterval", "60");
                config.put("ServerAliveCountMax", "10");
                localSession.setConfig(config);
                localSession.connect();
                Channel channel = localSession.openChannel("sftp");
                channel.connect();
                localSftpChannel = (ChannelSftp) channel;

                java.io.InputStream inputStream = localSftpChannel.get(remotePath);
                java.io.File downloadsDir = getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS);
                java.io.File localFile = new java.io.File(downloadsDir, fileName);
                java.io.File parentDir = localFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                java.io.OutputStream outputStream = new java.io.FileOutputStream(localFile);
                byte[] buffer = new byte[65536];
                int bytesRead;
                long total = 0;
                long downloaded = 0;
                int lastPercent = 0;
                try {
                    total = localSftpChannel.lstat(remotePath).getSize();
                } catch (Exception ignored) {}
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (cancelled[0]) break;
                    outputStream.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;
                    if (total > 0) {
                        int percent = (int) ((downloaded * 100) / total);
                        if (percent != lastPercent && percent % 1 == 0) {
                            lastPercent = percent;
                            runOnUiThread(() -> {
                                percentView.setText(percent + "% done");
                                progressBar.setProgress(percent);
                            });
                        }
                    }
                }
                outputStream.flush();
                inputStream.close();
                outputStream.close();
                runOnUiThread(() -> {
                    title.setText("Download complete");
                    percentView.setText("");
                    progressBar.setProgress(100);
                    dialog.setCancelable(true);
                    dialog.dismiss();
                    Toast.makeText(this, "Downloaded to: " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                android.util.Log.e("SFTPDownload", "Download error (app external storage)", e);
                runOnUiThread(() -> {
                    percentView.setText("Error: " + e.getMessage());
                    Toast.makeText(this, "Download error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.setCancelable(true);
                    dialog.dismiss();
                });
            } finally {
                // Clean up SFTP channel/session
                if (localSftpChannel != null && localSftpChannel.isConnected()) localSftpChannel.disconnect();
                if (localSession != null && localSession.isConnected()) localSession.disconnect();
            }
        }).start();
        }

    // Download file to shared Downloads folder using MediaStore (Android Q+)
    private boolean downloadToSharedDownloads(String remotePath, String fileName) {
        android.content.ContentResolver resolver = getContentResolver();
        android.content.ContentValues contentValues = new android.content.ContentValues();
        contentValues.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            contentValues.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS);
            contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1);
        }
        android.net.Uri fileUri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        if (fileUri == null) {
            runOnUiThread(() -> Toast.makeText(this, "Failed to create file in Downloads", Toast.LENGTH_LONG).show());
            return false;
        }
        try {
            // Show progress dialog for this download
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_download_modern, null);
                builder.setView(dialogView);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();

                TextView title = dialogView.findViewById(R.id.downloadTitle);
                TextView fileNameView = dialogView.findViewById(R.id.downloadFileName);
                TextView percentView = dialogView.findViewById(R.id.downloadPercent);
                ProgressBar progressBar = dialogView.findViewById(R.id.downloadProgressBar);
                Button cancelButton = dialogView.findViewById(R.id.downloadCancelButton);

                title.setText("Downloading file...");
                fileNameView.setText(fileName);
                progressBar.setMax(100);
                progressBar.setProgress(0);

                final boolean[] cancelled = {false};
                cancelButton.setOnClickListener(v -> {
                    cancelled[0] = true;
                    dialog.dismiss();
                });

                new Thread(() -> {
                    Session localSession = null;
                    ChannelSftp localSftpChannel = null;
                    try {
                        JSch jsch = new JSch();
                        localSession = jsch.getSession(username, host, port);
                        localSession.setPassword(password);
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");
                        config.put("ServerAliveInterval", "60");
                        config.put("ServerAliveCountMax", "10");
                        localSession.setConfig(config);
                        localSession.connect();
                        Channel channel = localSession.openChannel("sftp");
                        channel.connect();
                        localSftpChannel = (ChannelSftp) channel;

                        java.io.InputStream inputStream = localSftpChannel.get(remotePath);
                        java.io.OutputStream outputStream = resolver.openOutputStream(fileUri);
                        if (outputStream == null) throw new Exception("Failed to open output stream");
                        byte[] buffer = new byte[65536];
                        int bytesRead;
                        long total = 0;
                        long downloaded = 0;
                        int lastPercent = 0;
                        try {
                            total = localSftpChannel.lstat(remotePath).getSize();
                        } catch (Exception ignored) {}
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            if (cancelled[0]) break;
                            outputStream.write(buffer, 0, bytesRead);
                            downloaded += bytesRead;
                            if (total > 0) {
                                int percent = (int) ((downloaded * 100) / total);
                                if (percent != lastPercent && percent % 1 == 0) {
                                    lastPercent = percent;
                                    runOnUiThread(() -> {
                                        percentView.setText(percent + "% done");
                                        progressBar.setProgress(percent);
                                    });
                                }
                            }
                        }
                        outputStream.flush();
                        inputStream.close();
                        outputStream.close();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            contentValues.clear();
                            contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0);
                            resolver.update(fileUri, contentValues, null, null);
                        }
                        runOnUiThread(() -> {
                            title.setText("Download complete");
                            percentView.setText("");
                            progressBar.setProgress(100);
                            dialog.setCancelable(true);
                            dialog.dismiss();
//                            Toast.makeText(this, "Downloaded to Downloads folder", Toast.LENGTH_LONG).show();
                        });
                    } catch (Exception e) {
                        android.util.Log.e("SFTPDownload", "Download error (shared downloads)", e);
                        resolver.delete(fileUri, null, null);
                        runOnUiThread(() -> {
                            percentView.setText("Error: " + e.getMessage());
                            Toast.makeText(this, "Download error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            dialog.setCancelable(true);
                            dialog.dismiss();
                        });
                    } finally {
                        if (localSftpChannel != null && localSftpChannel.isConnected()) localSftpChannel.disconnect();
                        if (localSession != null && localSession.isConnected()) localSession.disconnect();
                    }
                }).start();
            });
            return true;
        } catch (Exception e) {
            android.util.Log.e("SFTPDownload", "Download error (shared downloads)", e);
            resolver.delete(fileUri, null, null);
            runOnUiThread(() -> Toast.makeText(this, "Download error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            return false;
        }
    }
    private boolean selectionMode = false;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MaterialButton backButton, downloadButton;
    private com.example.sftpapp.SftpFileAdapter adapter;
    private ChannelSftp sftpChannel;
    private Session session;
    private String currentPath = ".";
    private String host, username, password;
    private int port;
    private List<android.net.Uri> localFileUris = new ArrayList<>();
    private List<FileToDownload> filesToDownload = new ArrayList<>();
    private int filePickerIndex = 0;

    private static class FileToDownload {
        String remotePath;
        String relativePath;
        ChannelSftp.LsEntry entry;
        FileToDownload(String remotePath, String relativePath, ChannelSftp.LsEntry entry) {
            this.remotePath = remotePath;
            this.relativePath = relativePath;
            this.entry = entry;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Hide the ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Request storage permissions at runtime
        // For Android 11+ (API 30+), Storage Access Framework (SAF) is used for file/folder access.
        // No need to request legacy storage permissions.
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            for (String perm : permissions) {
                if (checkSelfPermission(perm) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 200);
                    break;
                }
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        downloadButton = findViewById(R.id.downloadButton);
    pathHeader = findViewById(R.id.pathHeader);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SftpFileAdapter(new ArrayList<>(),
            this::onFileClicked,
            this::onFileLongPressed);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        host = intent.getStringExtra("host");
        port = intent.getIntExtra("port", 22);
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        connectAndListFiles(currentPath);

        backButton.setOnClickListener(v -> navigateUp());
        downloadButton.setOnClickListener(v -> downloadSelectedFiles());
    }

    private void connectAndListFiles(String path) {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                if (session == null || !session.isConnected()) {
                    JSch jsch = new JSch();
                    session = jsch.getSession(username, host, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
                    Channel channel = session.openChannel("sftp");
                    channel.connect();
                    sftpChannel = (ChannelSftp) channel;
                }
                List<ChannelSftp.LsEntry> files = sftpChannel.ls(path);
                runOnUiThread(() -> {
                    adapter.setFiles(files);
                    currentPath = path;
                    if (pathHeader != null) pathHeader.setText(path);
                    progressBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(BrowseActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }
    private TextView pathHeader;

    private void onFileClicked(ChannelSftp.LsEntry entry) {
        if (selectionMode) {
            adapter.toggleFileSelection(entry);
            // If no files are selected, turn off selection mode
            if (adapter.getSelectedFiles().isEmpty()) {
                selectionMode = false;
            }
        } else {
            if (entry.getAttrs().isDir()) {
                connectAndListFiles(currentPath + "/" + entry.getFilename());
            }
            // If file, do nothing on single tap unless in selection mode
        }
    }

    // Long press to start selection mode
    private void onFileLongPressed(ChannelSftp.LsEntry entry) {
        if (!selectionMode) {
            selectionMode = true;
            adapter.toggleFileSelection(entry);
        }
    }

    private void navigateUp() {
        if (!currentPath.equals(".")) {
            int lastSlash = currentPath.lastIndexOf("/");
            String parent = lastSlash > 0 ? currentPath.substring(0, lastSlash) : ".";
            connectAndListFiles(parent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            android.net.Uri baseFolderUri = data.getData();
            startMultiFileDownloadToFolder(filesToDownload, baseFolderUri);
        }
    }

    private void downloadSelectedFiles() {
        List<ChannelSftp.LsEntry> selectedEntries = adapter.getSelectedFiles();
        if (selectedEntries.isEmpty()) {
            Toast.makeText(this, "No files/folders selected", Toast.LENGTH_SHORT).show();
            return;
        }
        // Recursively collect all files from selected entries
        filesToDownload.clear();
        for (ChannelSftp.LsEntry entry : selectedEntries) {
            String basePath = currentPath.equals(".") ? "" : currentPath;
            collectFilesRecursively(basePath, entry.getFilename(), entry);
        }
        new Thread(() -> {
            try {
                // Ensure SFTP channel is connected for all downloads
                if (session == null || !session.isConnected()) {
                    JSch jsch = new JSch();
                    session = jsch.getSession(username, host, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
                    Channel channel = session.openChannel("sftp");
                    channel.connect();
                    sftpChannel = (ChannelSftp) channel;
                }
                for (FileToDownload file : filesToDownload) {
                    // Download only files, skip folders
                    if (!file.entry.getAttrs().isDir()) {
                        downloadToAppExternalStorage(file.remotePath, file.relativePath);
                        downloadToSharedDownloads(file.remotePath, file.relativePath);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("SFTPDownload", "Error ensuring SFTP connection", e);
                runOnUiThread(() -> Toast.makeText(this, "SFTP connection error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Recursively collect files from folders, preserving relative paths
    private void collectFilesRecursively(String parentPath, String name, ChannelSftp.LsEntry entry) {
        String remotePath = parentPath.isEmpty() ? name : parentPath + "/" + name;
        String relativePath = remotePath.substring(currentPath.length()).replaceFirst("^/", "");
        if (entry.getAttrs().isDir()) {
            try {
                @SuppressWarnings("unchecked")
                List<ChannelSftp.LsEntry> children = sftpChannel.ls(remotePath);
                for (ChannelSftp.LsEntry child : children) {
                    String childName = child.getFilename();
                    if (!childName.equals(".") && !childName.equals("..")) {
                        collectFilesRecursively(remotePath, childName, child);
                    }
                }
            } catch (Exception e) {
                // Ignore errors for empty or inaccessible folders
            }
        } else {
            filesToDownload.add(new FileToDownload(remotePath, relativePath, entry));
        }
    }

    private void promptForNextFileUri() {
        FileToDownload file = filesToDownload.get(filePickerIndex);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, file.relativePath);
        startActivityForResult(intent, 101);
    }

    private void startMultiFileDownload(List<FileToDownload> files, List<android.net.Uri> uris) {
        if (files.size() != uris.size()) {
            Toast.makeText(this, "File/URI mismatch", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView text1 = dialogView.findViewById(android.R.id.text1);
        TextView text2 = dialogView.findViewById(android.R.id.text2);

        new Thread(() -> {
            for (int i = 0; i < files.size(); i++) {
                final int fileIndex = i;
                final FileToDownload file = files.get(i);
                final android.net.Uri uri = uris.get(i);
                if (uri == null) continue;
                runOnUiThread(() -> {
                    text1.setText("Downloading: " + file.relativePath + " (" + (fileIndex + 1) + "/" + files.size() + ")");
                    text2.setText("0% done");
                });
                try {
                    java.io.InputStream inputStream = sftpChannel.get(file.remotePath);
                    android.content.ContentResolver resolver = getContentResolver();
                    java.io.OutputStream outputStream = resolver.openOutputStream(uri);
                    if (outputStream == null) throw new Exception("Failed to open output stream");
                    long total = file.entry.getAttrs().getSize();
                    long downloaded = 0;
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        downloaded += bytesRead;
                        final int percent = total > 0 ? (int) ((downloaded * 100) / total) : 0;
                        runOnUiThread(() -> text2.setText(percent + "% done"));
                    }
                    inputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    final String errorMsg = e.getMessage();
                    runOnUiThread(() -> text2.setText("Error: " + errorMsg));
                }
            }
            runOnUiThread(() -> {
                text1.setText("All downloads complete");
                text2.setText("");
                dialog.setCancelable(true);
                dialog.dismiss();
            });
        }).start();
    }

    // New method: download all files to the selected folder, preserving structure
    private void startMultiFileDownloadToFolder(List<FileToDownload> files, android.net.Uri baseFolderUri) {
        if (baseFolderUri == null) {
            Toast.makeText(this, "No folder selected", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if folder is writable
        try {
            android.content.ContentResolver resolver = getContentResolver();
            android.net.Uri testFile = android.provider.DocumentsContract.createDocument(
                resolver, baseFolderUri, "application/octet-stream", "sfto_test.tmp");
            if (testFile == null) {
                throw new Exception();
            }
            resolver.delete(testFile, null, null);
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this,
                    "Selected folder is not writable. Please select a folder like Downloads or Documents. Some system folders may not allow writing.",
                    Toast.LENGTH_LONG).show();
                // Optionally, show a dialog to guide the user
                new AlertDialog.Builder(this)
                    .setTitle("Folder Not Writable")
                    .setMessage("The selected folder cannot be written to. Please select a folder such as Downloads or Documents for saving files.")
                    .setPositiveButton("OK", null)
                    .show();
            });
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_download_modern, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView title = dialogView.findViewById(R.id.downloadTitle);
        TextView fileNameView = dialogView.findViewById(R.id.downloadFileName);
        TextView percentView = dialogView.findViewById(R.id.downloadPercent);
        ProgressBar progressBar = dialogView.findViewById(R.id.downloadProgressBar);
        Button cancelButton = dialogView.findViewById(R.id.downloadCancelButton);

    title.setText("Downloading files...");
    progressBar.setMax(100);
    progressBar.setProgress(0);

        final boolean[] cancelled = {false};
        cancelButton.setOnClickListener(v -> {
            cancelled[0] = true;
            dialog.dismiss();
        });

        new Thread(() -> {
            android.content.ContentResolver resolver = getContentResolver();
            for (int i = 0; i < files.size(); i++) {
                if (cancelled[0]) break;
                final int fileIndex = i;
                final FileToDownload file = files.get(i);
                runOnUiThread(() -> {
                    fileNameView.setText(file.relativePath + " (" + (fileIndex + 1) + "/" + files.size() + ")");
                    percentView.setText("0% done");
                    progressBar.setProgress(0);
                });
                try {
                    android.net.Uri fileUri = createFileWithFolders(resolver, baseFolderUri, file.relativePath);
                    if (fileUri == null) {
                        final String msg = "Failed to create file/folder for: " + file.relativePath;
                        runOnUiThread(() -> {
                            percentView.setText("Error: " + msg);
                            Toast.makeText(BrowseActivity.this, msg, Toast.LENGTH_SHORT).show();
                        });
                        continue;
                    }
                    java.io.InputStream inputStream = sftpChannel.get(file.remotePath);
                    java.io.OutputStream outputStream = resolver.openOutputStream(fileUri);
                    if (outputStream == null) {
                        final String msg = "Failed to open output stream for: " + file.relativePath;
                        runOnUiThread(() -> {
                            percentView.setText("Error: " + msg);
                            Toast.makeText(BrowseActivity.this, msg, Toast.LENGTH_SHORT).show();
                        });
                        continue;
                    }
                    long total = file.entry.getAttrs().getSize();
                    long downloaded = 0;
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        if (cancelled[0]) break;
                        outputStream.write(buffer, 0, bytesRead);
                        downloaded += bytesRead;
                        final int percent = total > 0 ? (int) ((downloaded * 100) / total) : 0;
                        runOnUiThread(() -> {
                            percentView.setText(percent + "% done");
                            progressBar.setProgress(percent);
                        });
                    }
                    inputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    final String errorMsg = e.getMessage();
                    runOnUiThread(() -> {
                        percentView.setText("Error: " + errorMsg);
                        Toast.makeText(BrowseActivity.this, "Download error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    });
                }
            }
            runOnUiThread(() -> {
                title.setText("All downloads complete");
                fileNameView.setText("");
                percentView.setText("");
                progressBar.setProgress(files.size());
                dialog.setCancelable(true);
                dialog.dismiss();
            });
        }).start();
    }

    // Helper: create file and subfolders inside baseFolderUri, returns file Uri
    private android.net.Uri createFileWithFolders(android.content.ContentResolver resolver, android.net.Uri baseFolderUri, String relativePath) {
        String[] parts = relativePath.split("/");
        android.net.Uri currentUri = baseFolderUri;
        for (int i = 0; i < parts.length - 1; i++) {
            currentUri = createOrFindFolder(resolver, currentUri, parts[i]);
            if (currentUri == null) return null;
        }
        // Create the file
        String fileName = parts[parts.length - 1];
        android.net.Uri fileUri = createFile(resolver, currentUri, fileName);
        return fileUri;
    }

    // Helper: create or find a folder inside parentUri
    private android.net.Uri createOrFindFolder(android.content.ContentResolver resolver, android.net.Uri parentUri, String folderName) {
        try {
            android.net.Uri uri = android.provider.DocumentsContract.createDocument(resolver, parentUri, android.provider.DocumentsContract.Document.MIME_TYPE_DIR, folderName);
            return uri;
        } catch (Exception e) {
            return null;
        }
    }

    // Helper: create a file inside parentUri
    private android.net.Uri createFile(android.content.ContentResolver resolver, android.net.Uri parentUri, String fileName) {
        try {
            android.net.Uri uri = android.provider.DocumentsContract.createDocument(resolver, parentUri, "application/octet-stream", fileName);
            return uri;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sftpChannel != null && sftpChannel.isConnected()) sftpChannel.disconnect();
        if (session != null && session.isConnected()) session.disconnect();
    }
}