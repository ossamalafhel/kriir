package com.mobility.demo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarsSearchWithinRadius {

  @NotNull
  @ApiModelProperty(value = "Latitude ex:45.40603", required = true)
  private Double lat;

  @NotNull
  @ApiModelProperty(value = "Longitude ex:3.3203", required = true)
  private Double lon;

  @NotNull
  @ApiModelProperty(value = "Distance en km ex:10", required = true)
  private Integer dist;

}
