ipackage edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;
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
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  
  private Long id;
  @Column(nullable = false)
  private Long itemid;
  
  @Column(nullable = false)
  private String reviewerEmail;
  
  @Column(nullable = false)
  private int stars;

  @Column(nullable = false)
  private LocalDateTime datereviewed;

  @Column(length = 999)
  private String comments;
}
