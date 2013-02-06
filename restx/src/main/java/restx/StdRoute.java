package restx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: xavierhanin
 * Date: 1/19/13
 * Time: 8:10 AM
 */
public abstract class StdRoute implements RestxRoute {
    private final String name;
    private final RestxRouteMatcher matcher;
    private final ObjectMapper mapper;

    public StdRoute(String name, ObjectMapper mapper, RestxRouteMatcher matcher) {
        this.name = checkNotNull(name);
        this.mapper = checkNotNull(mapper);
        this.matcher = checkNotNull(matcher);
    }

    @Override
    public boolean route(RestxRequest req, RestxResponse resp, RouteLifecycleListener listener) throws IOException {
        String path = req.getRestxPath();
        Optional<RestxRouteMatch> match = matcher.match(req.getHttpMethod(), path);
        if (match.isPresent()) {
            listener.onRouteMatch(this);
            Optional<?> result = doRoute(req, match.get());
            if (result.isPresent()) {
                resp.setStatus(200);
                resp.setContentType("application/json");
                Object value = result.get();
                if (value instanceof Iterable) {
                    value = Lists.newArrayList((Iterable) value);
                }
                listener.onBeforeWriteContent(this);
                writeValue(mapper, resp.getWriter(), value);
                resp.getWriter().close();
            } else {
                resp.setStatus(404);
                resp.setContentType("text/plain");
                resp.getWriter().println("Route matched, but resource " + path + " not found.");
                resp.getWriter().println("Matched route: " + this);
                resp.getWriter().println("Path params: " + match.get().getPathParams());
                resp.getWriter().close();
            }
            return true;
        }
        return false;
    }

    protected void writeValue(ObjectMapper mapper, PrintWriter writer, Object value) throws IOException {
        mapper.writeValue(writer, value);
    }

    protected abstract Optional<?> doRoute(RestxRequest restxRequest, RestxRouteMatch match) throws IOException;

    @Override
    public String toString() {
        return matcher.toString() + " => " + name;
    }
}