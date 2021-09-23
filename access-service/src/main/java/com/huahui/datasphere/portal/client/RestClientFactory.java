package com.huahui.datasphere.portal.client;

public class RestClientFactory
{
    public static RestClient getClient(final RestClientType client, final String jobId) {
        if (client.equals(RestClientType.HTTP)) {
            return new HttpRestClient(jobId);
        }
        return new JerseyRestClient(jobId);
    }
}
