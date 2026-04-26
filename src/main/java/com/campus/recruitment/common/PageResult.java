package com.campus.recruitment.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private Long current;

    private Long size;

    private Long total;

    private Long pages;

    private List<T> records;
}
