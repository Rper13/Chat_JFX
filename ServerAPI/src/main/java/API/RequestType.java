package API;

public enum RequestType {

    MESSAGE(""),
    LOGIN("API:LOGIN:"),
    REGISTER("API:REGISTER:"),
    USER_INFO("API:USER_INFO:"),
    UPDATE_PROFILE("API:UPDATE_PROFILE"),
    GET_PROFILE("API:GET_PROFILE:");

    private final String identifier;

    RequestType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier(){
        return identifier;
    }

}
