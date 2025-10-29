package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
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
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private Long itemId;
  
  @Column(nullable = false)
  private String reviewerEmail;
  
  @Column(nullable = false)
  private int stars;
  
  @Column(nullable = false)
  private LocalDateTime dateReviewed;
  
  @Column(length = 1000)
  private String comments;
}
