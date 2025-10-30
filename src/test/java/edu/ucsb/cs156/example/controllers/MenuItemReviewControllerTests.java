package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)
public class MenuItemReviewControllerTests extends ControllerTestCase {

  @MockBean MenuItemReviewRepository menuItemReviewRepository;

  @MockBean UserRepository userRepository;

  // Authorization tests for /api/ucsbdates/admin/all
  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc
        .perform(get("/api/menuitemreview/all"))
        .andExpect(status().is(403)); // logged out users can't get all
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc.perform(get("/api/menuitemreview/all")).andExpect(status().is(200)); // logged
  }

  // @Test
  // public void logged_out_users_cannot_get_by_id() throws Exception {
  // mockMvc
  //    .perform(get("/api/ucsbdates?id=7"))
  //    .andExpect(status().is(403)); // logged out users can't get by id
  // }

  // Authorization tests for /api/ucsbdates/post
  // (Perhaps should also have these for put and delete)

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/menuitemreview/post")).andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(post("/api/menuitemreview/post"))
        .andExpect(status().is(403)); // only admins can post
  }

  // here

  // Test GET /all returns all reviews
  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_menu_item_reviews() throws Exception {
    // arrange
    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

    MenuItemReview review1 =
        MenuItemReview.builder()
            .id(1L)
            .itemId(1L)
            .reviewerEmail("example@example.com")
            .stars(5)
            .dateReviewed(ldt1)
            .comments("supa cool!")
            .build();

    MenuItemReview review2 =
        MenuItemReview.builder()
            .id(2L)
            .itemId(2L)
            .reviewerEmail("example2@example.com")
            .stars(3)
            .dateReviewed(ldt2)
            .comments("EH")
            .build();

    ArrayList<MenuItemReview> expectedReviews = new ArrayList<>();
    expectedReviews.addAll(Arrays.asList(review1, review2));

    when(menuItemReviewRepository.findAll()).thenReturn(expectedReviews);

    // act
    MvcResult response =
        mockMvc.perform(get("/api/menuitemreview/all")).andExpect(status().isOk()).andReturn();

    // assert

    verify(menuItemReviewRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expectedReviews);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  // Test POST creates a new review
  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void an_admin_user_can_post_a_new_menu_item_review() throws Exception {
    // arrange
    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

    MenuItemReview review1 =
        MenuItemReview.builder()
            .id(1L)
            .itemId(1L)
            .reviewerEmail("reviewer@example.com")
            .stars(5)
            .dateReviewed(ldt1)
            .comments("Delicious!")
            .build();

    when(menuItemReviewRepository.save(any(MenuItemReview.class))).thenReturn(review1);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/menuitemreview/post?itemId=1&reviewerEmail=reviewer@example.com&stars=5&dateReviewed=2022-01-03T00:00:00&comments=Delicious!")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(menuItemReviewRepository, times(1)).save(any(MenuItemReview.class));
    String expectedJson = mapper.writeValueAsString(review1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  // here: tests for remaining mutation coverage

  // mutation-- confirming setters
  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void test_that_post_sets_all_fields_correctly_using_argument_captor() throws Exception {
    // arrange
    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
    ArgumentCaptor<MenuItemReview> captor = ArgumentCaptor.forClass(MenuItemReview.class);

    MenuItemReview savedReview =
        MenuItemReview.builder()
            .id(1L)
            .itemId(27L)
            .reviewerEmail("captor@test.com")
            .stars(3)
            .dateReviewed(ldt)
            .comments("Captured!")
            .build();

    when(menuItemReviewRepository.save(any(MenuItemReview.class))).thenReturn(savedReview);

    // act
    mockMvc
        .perform(
            post("/api/menuitemreview/post?itemId=27&reviewerEmail=captor@test.com&stars=3&dateReviewed=2022-01-03T00:00:00&comments=Captured!")
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();

    // assert
    verify(menuItemReviewRepository).save(captor.capture());
    MenuItemReview capturedReview = captor.getValue();

    assertEquals(27L, capturedReview.getItemId());
    assertEquals("captor@test.com", capturedReview.getReviewerEmail());
    assertEquals(3, capturedReview.getStars());
    assertEquals(ldt, capturedReview.getDateReviewed());
    assertEquals("Captured!", capturedReview.getComments());
  }
}
