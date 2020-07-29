package com.khan.quizzer_onlinequizapp;

public class TestClass {

    private String setId,setName;
    private long order,time;
    private double socreDe,socreInc;
    private String mcqUrl,cqUrl;
    private String urlMedical, urlEngineering, urlPublic, urlPrivate;

    public TestClass(String setId, String setName, long order, String urlMedical, String urlEngineering, String urlPublic, String urlPrivate) {
        this.setId = setId;
        this.setName = setName;
        this.order = order;
        this.urlMedical = urlMedical;
        this.urlEngineering = urlEngineering;
        this.urlPublic = urlPublic;
        this.urlPrivate = urlPrivate;
    }

    public void setSocreDe(double socreDe) {
        this.socreDe = socreDe;
    }

    public void setSocreInc(double socreInc) {
        this.socreInc = socreInc;
    }

    public String getUrlMedical() {
        return urlMedical;
    }

    public void setUrlMedical(String urlMedical) {
        this.urlMedical = urlMedical;
    }

    public String getUrlEngineering() {
        return urlEngineering;
    }

    public void setUrlEngineering(String urlEngineering) {
        this.urlEngineering = urlEngineering;
    }

    public String getUrlPublic() {
        return urlPublic;
    }

    public void setUrlPublic(String urlPublic) {
        this.urlPublic = urlPublic;
    }

    public String getUrlPrivate() {
        return urlPrivate;
    }

    public void setUrlPrivate(String urlPrivate) {
        this.urlPrivate = urlPrivate;
    }

    public TestClass(String setId, String setName, long order) {
        this.setId = setId;
        this.setName = setName;
        this.order = order;
    }

    public TestClass(String setId, String setName,long order,double socreInc,double socreDe,long time,String mcqUrl,String cqUrl) {
        this.setId = setId;
        this.setName = setName;
        this.order = order;
        this.socreInc = socreInc;
        this.socreDe = socreDe;
        this.time = time;
        this.mcqUrl = mcqUrl;
        this.cqUrl = cqUrl;
    }

    public String getMcqUrl() {
        return mcqUrl;
    }

    public void setMcqUrl(String mcqUrl) {
        this.mcqUrl = mcqUrl;
    }

    public String getCqUrl() {
        return cqUrl;
    }

    public void setCqUrl(String cqUrl) {
        this.cqUrl = cqUrl;
    }

    public double getSocreInc() {
        return socreInc;
    }

    public void setSocreInc(long socreInc) {
        this.socreInc = socreInc;
    }

    public double getSocreDe() {
        return socreDe;
    }

    public void setSocreDe(long socreDe) {
        this.socreDe = socreDe;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}
