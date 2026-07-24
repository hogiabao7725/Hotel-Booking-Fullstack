package com.hogiabao7725.hotelbooking.service.transaction;

import com.hogiabao7725.hotelbooking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionalFileManager {

    private final FileStorageService fileStorageService;

    // Delete old file after successful database commit
    public void deleteAfterCommit(String path) {
        if (!StringUtils.hasText(path)) {
            return;
        }

        // Register synchronization if inside an active transaction
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteQuietly(path);
                }
            });
        } else {
            // No transaction: delete immediately to avoid orphaned files
            log.warn("No active transaction found for deleteAfterCommit. Deleting file immediately: {}", path);
            deleteQuietly(path);
        }
    }

    // Delete newly uploaded file if database transaction rollbacks
    public void deleteAfterRollback(String path) {
        if (!StringUtils.hasText(path)) {
            return;
        }

        // Register synchronization if inside an active transaction
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                deleteQuietly(path);
                            }
                        }
                    }
            );
        } else {
            log.warn("No active transaction found for deleteAfterRollback. File will not be deleted: {}", path);
        }
    }

    // Delete file quietly to prevent file storage failure from throwing exceptions and crashing the app
    private void deleteQuietly(String path) {
        try {
            fileStorageService.delete(path);
        } catch (Exception ex) {
            log.warn("Failed to clean up file from storage: {}", path, ex);
        }
    }
}
