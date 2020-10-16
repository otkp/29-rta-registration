/*
 * package org.epragati.rules.broker;
 * 
 * import java.util.HashMap; import java.util.Map;
 * 
 * import org.pycker.broker.MsgProducer; import
 * org.pycker.broker.vo.DestinationRequestVo; import
 * org.pycker.service.CommonService; import
 * org.pycker.service.RuleEngineService; import
 * org.pycker.service.util.RuleEngineObject; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.kafka.annotation.KafkaListener; import
 * org.springframework.messaging.handler.annotation.Payload; import
 * org.springframework.stereotype.Service;
 * 
 * import com.google.gson.Gson;
 * 
 * @Service public class MsgConsumer {
 * 
 * private static final Logger LOG = LoggerFactory.getLogger(MsgConsumer.class);
 * 
 * @Autowired private RuleEngineService ruleService;
 * 
 * @Autowired private CommonService commonService;
 * 
 * @Autowired private MsgProducer msgProducer;
 * 
 * @Autowired private Gson gson;
 * 
 * 
 * @SuppressWarnings("unchecked")
 * 
 * @KafkaListener(topics = "RULE-ENGINE") public void listen(@Payload String
 * message) {
 * 
 * LOG.debug("received message='{}'", message);
 * 
 * Map<String, String> map = new HashMap<>(); map = (Map<String, String>)
 * gson.fromJson(message, map.getClass());
 * 
 * RuleEngineObject rIbj = ruleService.execute(map);
 * 
 * sendNotification(rIbj); }
 * 
 * private void sendNotification(RuleEngineObject rIbj) {
 * 
 * if (rIbj != null) {
 * 
 * if (rIbj.isSendEmail()) {
 * 
 * DestinationRequestVo destinationRe = new DestinationRequestVo();
 * 
 * destinationRe.setProtocol("https");
 * 
 * Map<String, String> list = new HashMap<>(); list.put("Authorization",
 * "Bearer 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"
 * ); list.put("Content-Type", "application/json");
 * 
 * String abc =
 * "{\"from\":{\"email\":\"phani@example.com\"},\"subject\":\"Congratulations you have earned "
 * + rIbj.getPoints() +
 * " Points.\",\"personalizations\":[{\"to\":[{\"email\":\"pbattula@otsi-usa.com\"}]}],\"content\":[{\"type\":\"text/plain\",\"value\":\"Hey Phani, Congratulations you have earned "
 * + rIbj.getPoints() + " Points.\"}]}";
 * 
 * destinationRe.setHeaders(list); destinationRe.setProtocol("https");
 * destinationRe.setHost("api.sendgrid.com/v3/mail/send");
 * destinationRe.setEmailBody(abc);
 * 
 * try { msgProducer.send(commonService.toJson(destinationRe), "pycker-msg"); }
 * catch (Exception e) { e.printStackTrace(); } } } } }
 */