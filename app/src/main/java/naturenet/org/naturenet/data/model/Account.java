package naturenet.org.naturenet.data.model;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

public class Account {

    public Set<String> consent = Sets.newHashSet();

    public String username = null;

    public String name = null;

    public String password = null;

    public String id = null;

    @SerializedName("icon_url")
    public String iconUrl = null;

    public String affiliation = null;
}
