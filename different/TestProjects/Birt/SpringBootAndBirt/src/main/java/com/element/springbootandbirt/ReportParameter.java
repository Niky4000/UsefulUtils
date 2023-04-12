package com.element.springbootandbirt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Класс реализует сущность "Параметр отчета"
@Getter
@Setter
@AllArgsConstructor
public class ReportParameter {

    private String name;
    private String value;

}
