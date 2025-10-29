package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a JPA entity that represents a UCSBDiningCommonsMenuItem
 *
 * <p>A UCSBDiningCommonsMenuItem is a menu item at a UCSB dining commons
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbdiningcommons")
public class UCSBDiningCommons {
  @Id private String code;
  private String name;
  private boolean hasSackMeal;
  private boolean hasTakeOutMeal;
  private boolean hasDiningCam;
  private Double latitude;
  private Double longitude;
}
