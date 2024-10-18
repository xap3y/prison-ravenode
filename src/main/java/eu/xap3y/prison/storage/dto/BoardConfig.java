package eu.xap3y.prison.storage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BoardConfig {

    public String title;
    public List<String> lines;

}
