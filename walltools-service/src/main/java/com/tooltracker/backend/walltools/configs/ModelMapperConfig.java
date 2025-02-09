package com.tooltracker.backend.walltools.configs;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tooltracker.backend.walltools.dtos.MovementDTO;
import com.tooltracker.backend.walltools.dtos.ToolDTO;
import com.tooltracker.backend.walltools.dtos.ToolDetailsDTO;
import com.tooltracker.backend.walltools.entities.Movement;
import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.entities.ToolDetails;

@Configuration
public class ModelMapperConfig { // REF ::
                                 // https://modelmapper.org/user-manual/property-mapping/#skipping-properties
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper ret = new ModelMapper();

        // SET: Specific Configuration when Mapping (ToolDTO to Tool Entity) to skip
        // Tool property that generated by JPA for one to one relation

        // USED IN createTool()
        // When we convert ToolDTO to Entity we want to ignore tool prop with annotation
        // @OneToOne in tool Details because its bidrectional relationship & we should
        // not overwrite it with modelmapper & its handelt automatic by JPA
        /*
         * When Mapping
         * ToolDTO ---> ToolEntity
         * [1] Skip overwriting ID : its autogenerated by jpa
         * [2] Skip any set to movements
         * Skip overwriting ToolDetails because its bidirectional Relationship with
         * Tool, every tool has its
         * own tooldetails and every tooldetails should be related with one tool (one to
         * one) relation & overwriting
         * tooldetails by mapping will break the relation and create new instance of
         * tooldetails
         */
        ret.addMappings(new PropertyMap<ToolDTO, Tool>() {
            protected void configure() {
                skip(destination.getId());
                skip(destination.getMovements());
                skip(destination.getToolDetails().getTool());
            }
        });

        /*
         * When Mapping
         * MovemenDTO ----> Movement
         * Skip id: because its auto generated by JPA
         */
        ret.addMappings(new PropertyMap<MovementDTO, Movement>() {
            protected void configure() {
                skip(destination.getId());
            }
        });

        /*
         * When Mapping
         * ToolDetailsDTO ----> ToolDetails
         * Skip Null Values
         */

        ret.addMappings(new PropertyMap<ToolDetailsDTO, ToolDetails>() {
            protected void configure() {
                when(prop -> prop.getSource() != null).map().setDescription(source.getDescription());
                when(prop -> prop.getSource() != null).map().setWidth(source.getWidth());
                when(prop -> prop.getSource() != null).map().setHeight(source.getHeight());
                when(prop -> prop.getSource() != null).map().setWeight(source.getWeight());
                when(prop -> prop.getSource() != null).map().setImg(source.getImg());
            }
        });

        /*
         * When Mapping from Movement To Movement Dto then inkect createdAt which
         * spicify the pickUp date
         */
        ret.addMappings(new PropertyMap<Movement, MovementDTO>() {
            protected void configure() {
                when(prop -> prop.getDestination() == null).map().setPickupDate(source.getCreatedAt());
            }
        });
        return ret;
    }
}
