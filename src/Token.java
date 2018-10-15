public class Token {


    private Object payload;

    public Token(Object payload) {

        this.payload = payload;

    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
