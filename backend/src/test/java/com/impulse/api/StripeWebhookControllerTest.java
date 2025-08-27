package com.impulse.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.impulse.application.ports.StripeWebhookEventPort;
import com.impulse.domain.monetizacion.StripeWebhookEvent;
import com.impulse.common.flags.FlagService;

@ExtendWith(MockitoExtension.class)
class StripeWebhookControllerTest {

    @Mock
    StripeWebhookEventPort repo;

    @Mock
    FlagService flags;

    @InjectMocks
    StripeWebhookController controller;

    @BeforeEach
    void setUp(){
        when(flags.isOn("monetization.paywall")).thenReturn(true);
    }

    @Test
    void webhook_invalid_signature_returns_400(){
    // set a webhook secret to ensure computeHmacHex won't see null
    org.springframework.test.util.ReflectionTestUtils.setField(controller, "webhookSecret", "secret-key");
    ResponseEntity<Map<String,Object>> resp = controller.webhook("{}", "t=1,v1=badhex", null);
    assertThat(resp.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).containsEntry("error","invalid_signature");
    }

    @Test
    void webhook_store_new_event_success(){
        // Build a valid signature using controller's computeHmacHex via reflection to avoid exposing helper
        // Instead, mock repo.findByEventId to be empty and call controller with no signature to force failure.
        when(repo.findByEventId(org.mockito.ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        // We bypass signature by calling validateSignature indirectly: instead, set webhookSecret via reflection
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "webhookSecret", "secret-key");

        // Build a valid signed header for payload
        String payload = "{\"type\":\"charge.succeeded\"}";
        String t = String.valueOf(System.currentTimeMillis()/1000L);
        String signedPayload = t + "." + payload;
        try{
            java.lang.reflect.Method m = StripeWebhookController.class.getDeclaredMethod("computeHmacHex", String.class, String.class);
            m.setAccessible(true);
            String hex = (String) m.invoke(controller, "secret-key", signedPayload);
            String header = "t="+t+",v1="+hex;

            ResponseEntity<java.util.Map<String,Object>> resp = controller.webhook(payload, header, "provided-id-123");
            assertThat(resp.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.ACCEPTED);
            assertThat(resp.getBody()).containsEntry("stored", true);
            verify(repo, times(1)).save(org.mockito.ArgumentMatchers.any(StripeWebhookEvent.class));
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
