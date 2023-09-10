package com.ados.XAPI.xapi;

import lombok.Data;

@Data
public class XStatement {
    private XActor actor;
    private XVerb verb;
    private XObject object;
    private XResult result;
}
