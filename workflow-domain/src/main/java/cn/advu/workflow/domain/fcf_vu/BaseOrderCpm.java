package cn.advu.workflow.domain.fcf_vu;

import cn.advu.workflow.domain.base.AbstractEntity;

import java.math.BigDecimal;

public class BaseOrderCpm extends AbstractEntity {

    private Integer orderId;

    private String orderCpmType; // 1，客户需求CPM 2,执行排期CPM 3，第三方检测CPM 4，采购框架CPM 5，需求框架合同  6，表示单采CPM

    private Integer mediaId;

    private BigDecimal mediaPrice;

    private BigDecimal firstPrice;

    private Integer adTypeId;

    private Integer cpm;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderCpmType() {
        return orderCpmType;
    }

    public void setOrderCpmType(String orderCpmType) {
        this.orderCpmType = orderCpmType;
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public BigDecimal getMediaPrice() {
        return mediaPrice;
    }

    public void setMediaPrice(BigDecimal mediaPrice) {
        this.mediaPrice = mediaPrice;
    }

    public BigDecimal getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(BigDecimal firstPrice) {
        this.firstPrice = firstPrice;
    }

    public Integer getAdTypeId() {
        return adTypeId;
    }

    public void setAdTypeId(Integer adTypeId) {
        this.adTypeId = adTypeId;
    }

    public Integer getCpm() {
        return cpm;
    }

    public void setCpm(Integer cpm) {
        this.cpm = cpm;
    }

}