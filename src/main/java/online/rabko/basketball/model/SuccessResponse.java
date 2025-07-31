package online.rabko.basketball.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Success response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuccessResponse {

  private boolean success;
}
