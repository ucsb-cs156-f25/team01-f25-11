package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "menuitemreviews")
public class MenuItemReview {
  @Id private String id;
  private Long itemid;
  private String reviewerEmail;
  private int stars;
  private LocalDateTime datereviewed;
  private String comments;
}
