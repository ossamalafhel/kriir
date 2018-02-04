package com.mobility.demo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSave {


  @ApiModelProperty(value = "id of the car if exist", required = true)
  private String id;

  @NotNull
  @ApiModelProperty(value = "Latitude ex:45.40603", required = true)
  private Double x;

  @NotNull
  @ApiModelProperty(value = "Longitude ex:3.3203", required = true)
  private Double y;

}
