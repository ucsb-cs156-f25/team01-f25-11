package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {
  @MockBean UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;
  @MockBean UserRepository userRepository;

  // Get All Tests
  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc
        .perform(get("/api/ucsb-dining-commons-menu-items/all"))
        .andExpect(status().is(403)); // logged out users can't get all
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc.perform(get("/api/ucsb-dining-commons-menu-items/all")).andExpect(status().is(200));
    // logged in users cannot get in if not admin
  }

  // Get by ID Tests
  @Test
  public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc
        .perform(get("/api/ucsb-dining-commons-menu-items?id=7"))
        .andExpect(status().is(403)); // logged out users can't get by id
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

    UCSBDiningCommonsMenuItem ucsbMenuItem =
        UCSBDiningCommonsMenuItem.builder()
            .name("food1")
            .diningCommonsCode("dining1")
            .station("station1")
            .build();

    when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L)))
        .thenReturn(Optional.of(ucsbMenuItem));

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsb-dining-commons-menu-items?id=7"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(7L));
    String expectedJson = mapper.writeValueAsString(ucsbMenuItem);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

    // arrange

    when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsb-dining-commons-menu-items?id=7"))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert

    verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(7L));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("UCSBDiningCommonsMenuItem with id 7 not found", json.get("message"));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_ucsbmenuitems() throws Exception {

    UCSBDiningCommonsMenuItem ucsbMenuItem1 =
        UCSBDiningCommonsMenuItem.builder()
            .name("food1")
            .diningCommonsCode("dining1")
            .station("station1")
            .build();

    UCSBDiningCommonsMenuItem ucsbMenuItem2 =
        UCSBDiningCommonsMenuItem.builder()
            .name("food2")
            .diningCommonsCode("dining2")
            .station("station2")
            .build();

    ArrayList<UCSBDiningCommonsMenuItem> expectedMenuItems = new ArrayList<>();
    expectedMenuItems.addAll(Arrays.asList(ucsbMenuItem1, ucsbMenuItem2));

    when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(expectedMenuItems);

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsb-dining-commons-menu-items/all"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expectedMenuItems);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  // Post Tests

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/ucsb-dining-commons-menu-items/post")).andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(post("/api/ucsb-dining-commons-menu-items/post"))
        .andExpect(status().is(403)); // only admins can post
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void an_admin_user_can_post_a_new_menuitem() throws Exception {

    UCSBDiningCommonsMenuItem ucsbMenuItem1 =
        UCSBDiningCommonsMenuItem.builder()
            .name("food1")
            .diningCommonsCode("dining1")
            .station("station1")
            .build();

    when(ucsbDiningCommonsMenuItemRepository.save(eq(ucsbMenuItem1))).thenReturn(ucsbMenuItem1);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsb-dining-commons-menu-items/post?name=food1&diningCommonsCode=dining1&station=station1")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(ucsbMenuItem1);
    String expectedJson = mapper.writeValueAsString(ucsbMenuItem1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }
}
