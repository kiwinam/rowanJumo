package smart.rowan.apiroute;

import java.util.Map;


class HttpResponse {
    // the request url
    String url;

    // the requester ip
    String origin;

    // all headers that have been sent
    Map headers;

    // url arguments
    Map args;

    // post form parameters
    Map form;

    // post body json
    Map json;
}
