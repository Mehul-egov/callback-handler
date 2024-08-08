package org.qwikpe.callback.handler.record;

public record DataPushRecord(int pageNumber, int pageCount, String transactionId,
                             String content, String careContextReference, String dataPushUrl,
                             String expiry) {
}
