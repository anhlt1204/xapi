package com.ados.XAPI.xapi;

import lombok.Data;

@Data
public class XResult {
    private String response;
    private XScore score;
    private boolean success;
}
