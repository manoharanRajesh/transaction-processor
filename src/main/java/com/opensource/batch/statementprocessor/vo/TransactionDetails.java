package com.opensource.batch.statementprocessor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "record")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionDetails {

    @XmlElement(name = "accountNumber")
    private String accountNumber;
    @XmlAttribute(name = "reference")
    private String referenceId;
    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "startBalance")
    private BigDecimal startBalance;
    @XmlElement(name = "endBalance")
    private BigDecimal endBalance;
    @XmlElement(name = "mutation")
    private BigDecimal mutation;
    private boolean duplicate;
    private boolean wrongEndBalance;
    private String errorDescription;

}
