package it.thesis.springboot.dto.criteria;

import lombok.Data;

import java.util.List;

@Data
public class SearchAccountCriteria {
    private List<Long> roleIds;
}