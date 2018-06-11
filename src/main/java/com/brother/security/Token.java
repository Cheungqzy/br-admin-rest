package com.brother.security;

import java.util.Date;

/**
 * Created by Coldmoon on 2015/10/30.
 */
public class Token {
    private Long memberId;
    private Date issueDate;

    public Long getMemberId() {
        return this.memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public Date getIssueDate() {
        return this.issueDate;
    }
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}
