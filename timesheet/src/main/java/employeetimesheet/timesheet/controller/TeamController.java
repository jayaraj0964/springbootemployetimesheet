package employeetimesheet.timesheet.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeetimesheet.timesheet.entity.Teams;
// import employeetimesheet.timesheet.service.CustomUserDetailsService;
import employeetimesheet.timesheet.service.TeamService;


@RestController
@RequestMapping("/api")
public class TeamController {

    
    // private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
   
   @Autowired
   TeamService teamService;
   
   @GetMapping("/getallteams")
   public List<Teams> getalllteams(){
    return teamService.getallteams();

   }


   @GetMapping("/getteamperson/{id}")
   public Teams getteammember(@PathVariable Long id){
    return teamService.getteamperson(id);
   }
   
   @PostMapping("/post")
   public Teams postteam(@RequestBody Teams teams){
    // logger.info("team name"+teams.getTeamname());
    return teamService.postteams(teams);
   }

   
   @PutMapping("/update/{id}")
   public Teams puttTeams(@RequestBody Teams team, @PathVariable Long id){
    return teamService.updateTeams(id,team);
   }

   @DeleteMapping("/delete/{id}")
   public void  deleteTeams(@PathVariable Long id){
    teamService.deleteteams(id);
   }


   @GetMapping("/serachteamskey/{keyword}")
   public List <Teams> serachTeamskeyword(@PathVariable String keyword ){
    return teamService.searchwithteamkeyword(keyword);
   }

   @GetMapping("/serachteams/{name}")
   public List <Teams> serchteamwithname(@PathVariable String name){
    return teamService.searchwithteamname(name);
   }
}
