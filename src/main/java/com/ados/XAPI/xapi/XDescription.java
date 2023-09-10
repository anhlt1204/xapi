package com.ados.XAPI.xapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class XDescription {
    @JsonProperty("en-us")
    public String en_us;
}
