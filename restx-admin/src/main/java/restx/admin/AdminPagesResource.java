package restx.admin;

import com.google.common.collect.Lists;
import restx.annotations.GET;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.factory.Factory;

import javax.inject.Inject;
import java.util.List;

/**
 * User: xavierhanin
 * Date: 4/7/13
 * Time: 1:54 PM
 */
@Component @RestxResource
public class AdminPagesResource {
    private final Iterable<AdminPage> pages;

    @Inject
    public AdminPagesResource(Factory factory) {
        this(factory.queryByClass(AdminPage.class).findAsComponents());
    }

    public AdminPagesResource(Iterable<AdminPage> pages) {
        this.pages = pages;
    }

    @GET("/@/pages")
    public Iterable<AdminPage> findPages(@Param(kind = Param.Kind.CONTEXT, value = "baseUri") String baseUri) {
        List<AdminPage> rootedPages = Lists.newArrayList();
        for (AdminPage page : pages) {
            rootedPages.add(page.rootOn(baseUri));
        }
        return rootedPages;
    }
}
