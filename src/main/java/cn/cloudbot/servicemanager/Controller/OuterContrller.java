package cn.cloudbot.servicemanager.Controller;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

public class OuterContrller {
    @Autowired
    private ServiceManager ServiceManagerInstance;
    private GroupManager GroupManagerInstance;

    @RequestMapping(path = "/groups", method = RequestMethod.GET)
    public @ResponseBody
    Collection<Basegroup> list_groups() {
        return GroupManagerInstance.listGroup();
    }


    @GetMapping(path = "/groups/{group_id}")
    public @ResponseBody BaseGroup getGroup(@PathVariable("group_id") String group_id) {
        return GroupManagerInstance.getGroupWithException(group_id);
    }


    @DeleteMapping(path = "/groups/{group_id}")
    public ResponseEntity deleteGroup(@PathVariable("group_id") String group_id) {
        boolean exists = GroupManagerInstance.removeGroup(group_id);

        HttpStatus status;
        if (exists ) {
            status = HttpStatus.NO_CONTENT;
        } else {
            status = HttpStatus.NOT_FOUND;
        }

//        ResponseEntity entity =
        return new ResponseEntity(status);
    }

    @PostMapping(path = "/groups")
    public ResponseEntity<BaseGroup> createGroup(@RequestBody GroupData groupData) {
        BaseGroup group = GroupManager.createGroup(groupData.getgroup_type());
        if (group.getGroup_list() != null) {
            for (Group group:
                    group.getGroup_list()) {
                group.addGroup(group);
            }
        }
        return new ResponseEntity<BaseGroup>(group, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/groups/deleteservs")
    public ResponseEntity deleteServes(@RequestBody BatchGroupServeCommand deleteGroupServeCommand) {
        BaseGroup group = groupManagerInstance.getGroupWithException(deleteGroupServeCommandCommand.getGroup_id());
        for (Group group:
                deleteGroupServeCommandCommand.getDelete_groups()) {
            group.removeGroupServe(group,serve);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/groups/addservs")
    public ResponseEntity addServs(@RequestBody BatchGroupServeCommand addGroupServeCommand) {
        BaseGroup group = groupManagerInstance.getGroupWithException(addGroupServeCommand.getGroup_id());

        for (Group group:
                addGroupServeCommandCommand.getDelete_servs()) {
            group.addGroupServe(group);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

//    @DeleteMapping(path = "/group")
//    public ResponseEntity deleteAll() {
//        Basegroup group = groupManagerInstance.getgroupWithException()
//    }



    @ExceptionHandler(GroupNotFound.class)
    public ResponseEntity<Error> groupNotFoundError(GroupNotFound groupNotFound) {
        String group_name = groupNotFound.getGroup_name();
        return new ResponseEntity<>(new Error(String.format("%s not found", group_name)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EnumValueException.class)
    public ResponseEntity<Error> ValueError(EnumValueException value) {
        return new ResponseEntity<Error>(new Error(String.format("%s not as excepted", value.getValue().toString())), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class BatchGroupServeCommand {
    private Collection<serve> delete_serves;
    private String group_id;

    public Collection<serve> getDelete_serves() {
        return delete_serves;
    }

    public void setDelete_serves(Collection<serve> delete_serves) {
        this.delete_serves = delete_serves;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class GroupData {
    //    private String group_id;
    private String group_type; // wechat or qq
    private Collection<serve> associated_serves;

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public Collection<serve> getAssociated_serves() {
        return associated_serves;
    }

    public void setAssociated_serves(Collection<serve> associated_serves) {
        this.associated_serves = associated_serves;
    }
}