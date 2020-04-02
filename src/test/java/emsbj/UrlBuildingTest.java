package emsbj;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UrlBuildingTest {
    @Autowired
    private Extensions extensions;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void adminUsersUrl() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("")
            .with(mockHttpServletRequest -> {
                String url = extensions.au().users();
                Assert.assertTrue(url != null && url.length() > 0);
                return mockHttpServletRequest;
            })
        ).andReturn();
    }
}
