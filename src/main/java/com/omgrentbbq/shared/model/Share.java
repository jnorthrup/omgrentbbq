package com.omgrentbbq.shared.model;

public class Share extends Memento {
    public static enum ShareType {
        fixedAmount, pieShare
    }

    private Membership membership;
    private ShareType shareType;
    private Float amount;


    public Share() {
    }

    public Share(ShareType shareType, Membership membership, Float amount) {
        this.setShareType(shareType);
        this.setMembership(membership);
        this.setAmount(amount);
    }

    public Membership getMembership() {
        return $("membership");
    }

    public ShareType getShareType() {
        return $("shareType");
    }

    public Float getAmount() {
        return $("amount");
    }

    public void setMembership(Membership membership) {
        $("membership", membership);
    }

    public void setShareType(ShareType shareType) {
        $("shareType", shareType);
    }

    public void setAmount(Float amount) {
        $("amount", amount);
    }
}
