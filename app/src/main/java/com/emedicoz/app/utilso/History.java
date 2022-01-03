package com.emedicoz.app.utilso;

import com.emedicoz.app.pubnub.PubSubPojo;
import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class History {

    public static final int TOP_ITEM_OFFSET = 3;
    private static final int CHUNK_SIZE = 50;
    private static final AtomicBoolean LOADING = new AtomicBoolean(false);

    private History() {
    }

    // tag::HIS-2[]
    public static void getAllMessages(PubNub pubNub, final String channel,
                                      final CallbackSkeleton callback) {
        pubNub.history()
                .channel(channel) // where to fetch history from
                .count(50) // how many items to fetch
                .includeTimetoken(false)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(final PNHistoryResult result, final PNStatus status) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!status.isError() && !result.getMessages().isEmpty()) {
                                    List<PubSubPojo> messages = new ArrayList<>();
                                    for (PNHistoryItemResult message : result.getMessages()) {
                                        try {
                                            JsonNode jsonMsg = message.getEntry();
                                            PubSubPojo msg = JsonUtil.convert(jsonMsg, PubSubPojo.class);
                                            messages.add(msg);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    callback.handleResponse(messages);
                                }
                            }
                        }).start();

                    }
                });
    }

    public static boolean isLoading() {
        return LOADING.get();
    }
    // end::HIS-2[]

    // tag::HIS-3[]
/*    public static void chainMessages(List<PubSubPojo> list, int count) {

        int limit = count;
        if (limit > list.size()) {
            limit = list.size();
        }

        for (int i = 0; i < limit; i++) {
            PubSubPojo message = list.get(i);
            if (i > 0) {
                MessageHelper.chain(message, list.get(i - 1));
            }
        }
    }*/
    // end::HIS-3[]

    public static void setLoading(boolean loading) {
        LOADING.set(loading);
    }

    public abstract static class CallbackSkeleton {

        public abstract void handleResponse(List<PubSubPojo> messages);
    }

}
