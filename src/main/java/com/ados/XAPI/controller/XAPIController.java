package com.ados.XAPI.controller;

import com.ados.XAPI.xapi.XStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rusticisoftware.tincan.*;
import com.rusticisoftware.tincan.lrsresponses.StatementLRSResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XAPIController {

    @GetMapping("/test")
    public String test() {
        return "OK";
    }

    @PostMapping("/xapi/statements")
    public ResponseEntity<?> receiveXAPIStatement(@RequestBody String statement) {
        System.out.println("Received xAPI statement: " + statement);
        ObjectMapper om = new ObjectMapper();
        try {
            XStatement root = om.readValue(statement, XStatement.class);
            System.out.println(root);
            if (root != null) {
                RemoteLRS lrs = new RemoteLRS();

                lrs.setEndpoint("https://testxapi123.lrs.io/xapi/");
                lrs.setVersion(TCAPIVersion.V100);
                lrs.setUsername("testxapi");
                lrs.setPassword("testxapi");

                Agent agent = new Agent();
                if (root.getActor() != null) {
                    agent.setName(root.getActor().getName());
                    agent.setMbox(root.getActor().getMbox());
                }

                Verb verb = new Verb();
                if (root.getVerb() != null) {
                    verb.setId(root.getVerb().getId());
                    LanguageMap languageMap = new LanguageMap();
                    languageMap.put("en-us", root.getVerb().getDisplay().en_us);
                    verb.setDisplay(languageMap);
                }

                Activity activity = new Activity();
                ActivityDefinition definition = new ActivityDefinition();
                if (root.getObject() != null) {
                    activity.setId(root.getObject().getId());
                    LanguageMap languageMap = new LanguageMap();
                    languageMap.put("en-us", root.getObject().getDefinition().getName().en_us);
                    definition.setName(languageMap);
                    languageMap = new LanguageMap();
                    languageMap.put("en-us", root.getObject().getDefinition().getDescription().en_us);
                    definition.setDescription(languageMap);
                    activity.setDefinition(definition);
                }

                Result result = new Result();
                if (root.getResult() != null) {
                    Score score = new Score();
                    score.setMin((double) root.getResult().getScore().getMin());
                    score.setMax((double) root.getResult().getScore().getMax());
                    score.setRaw((double) root.getResult().getScore().getRaw());
                    score.setScaled((double) root.getResult().getScore().getScaled());
                    result.setScore(score);
                    result.setResponse(root.getResult().getResponse());
                    result.setSuccess(root.getResult().isSuccess());
                }

                Statement st = new Statement();
                st.setActor(agent);
                st.setVerb(verb);
                st.setObject(activity);
                st.setResult(result);

                StatementLRSResponse lrsRes = lrs.saveStatement(st);
                System.out.println(lrsRes);
                if (lrsRes.getSuccess()) {
                    // success, use lrsRes.getContent() to get the statement back
                    System.out.println("----> success");
                } else {
                    // failure, error information is available in lrsRes.getErrMsg()
                    System.out.println("----> fail");
                }

                System.out.println("Statement sent successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("OK");
    }

}
