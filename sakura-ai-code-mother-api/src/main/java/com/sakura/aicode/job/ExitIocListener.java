package com.sakura.aicode.job;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
@Component
public class ExitIocListener implements ApplicationListener<ContextClosedEvent> {
   @Override
   public void onApplicationEvent(ContextClosedEvent event) {
//       WebScreenshotUtil.destroy();
   }
}