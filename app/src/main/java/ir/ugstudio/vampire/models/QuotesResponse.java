package ir.ugstudio.vampire.models;

import java.util.List;

public class QuotesResponse extends BaseModel {
    private List<String> quotes;

    public QuotesResponse() {
    }

    public List<String> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<String> quotes) {
        this.quotes = quotes;
    }
}
