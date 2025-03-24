package com.example.project.service.ManageSchedule;

import com.example.project.DTO.ManageSchedule.GetProposalScheduleDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ProposalSchedule;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.ProposalSchedRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageProposalScheduleService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProposalSchedRepository proposalSchedRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    // delete all
    public boolean deleteAllProposalSchedule(String program) {

        List<String> projectIds = projectRepository.findByProjectIdAndProgram(program);

        System.out.print("🪸List delete project: " + projectIds);

        if(projectIds.isEmpty()) {
            System.out.println("No projects found program: " + program);
            return false;
        }

        int deletedCount =  proposalSchedRepository.deleteAllByProgram(projectIds);

        return deletedCount > 0;

    }

    // อย่าลืม filter เฉพาะ auto-gen รอดูตอน add ก่อน
    public Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> getProposalSchedule(String program){

        List<String> projectIds = projectRepository.findByProjectIdAndProgram(program);

        System.out.print("🪸List get project: " + program);

        if(projectIds.isEmpty()) {

            System.out.println("No projects found program: " + program);
            return new HashMap<>();

        }

        List<ProposalSchedule> projectListSchedule = proposalSchedRepository.findByProjectIds(projectIds);

        List<GetProposalScheduleDTO> dtoList = projectListSchedule.stream()

                .map(schedule -> {
                        List<ProjectInstructorRole> instructors = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(schedule.getProjectId());

                        List<String> instructorNames = instructors.stream()
                                .map(instructor -> instructor.getInstructor().getProfessorName())
                                .collect(Collectors.toList());

                return new GetProposalScheduleDTO(
                                    schedule.getProposalScheduleId(),
                                    schedule.getStartTime().toLocalTime(),
                                    schedule.getEndTime().toLocalTime(),
                                    schedule.getDate(),
                                    schedule.getStatus(),
                                    schedule.getProjectId(),
                                    schedule.getProject().getProgram(),
                                    schedule.getProject().getSemester(),
                                    schedule.getProject().getProjectTitle(),
                                    instructorNames,
                                    schedule.getRoom()
                        ); })
                .collect(Collectors.toList());

//        dtoList.sort((dto1, dto2) -> {
//            int dateCompare = dto1.getDate().compareTo(dto2.getDate());
//
//            // https://www.w3schools.com/java/tryjava.asp?filename=demo_ref_string_compareto
//            if(dateCompare != 0) {
//                return  dateCompare;
//            }
//
//            return dto1.getStartTime().compareTo(dto2.getStartTime());
//        });

        dtoList.sort(Comparator.comparing(GetProposalScheduleDTO::getDate).thenComparing(GetProposalScheduleDTO::getStartTime));

        System.out.println("🍭Sorted dto: ");
        for(GetProposalScheduleDTO dto: dtoList) {
            System.out.println("Date: " + dto.getDate() + ", Start Time: " + dto.getStartTime() +
                    ", End Time: " + dto.getEndTime() + ", ProjectId: " + dto.getProjectId());
        };

        Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> groupedData = new LinkedHashMap<>();

        for(GetProposalScheduleDTO dto : dtoList) {

            String date = dto.getDate();

            // ใช้ putIfAbsent ก็ได้
            if(!groupedData.containsKey(date)) {
                groupedData.put(date, new LinkedHashMap<>());
            }

            LocalTime startTime = dto.getStartTime();
            LocalTime endTime = dto.getEndTime();

            Pair<LocalTime, LocalTime> timeSlot = new Pair<>(startTime, endTime);

            if(!groupedData.get(date).containsKey(timeSlot)) {
                groupedData.get(date).put(timeSlot, new ArrayList<>());
            }

            groupedData.get(date).get(timeSlot).add(dto);
        }

        return addBreakTime(groupedData);
    }

    private static final int TIME_BREAK = 15;

    public Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> addBreakTime(Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> groupedData) {

        Map<LocalDate, Integer> dailySlotCount = new HashMap<>();

        Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> result = new LinkedHashMap<>();

        for(Map.Entry<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> entry : groupedData.entrySet()) {

            // get date
            String date = entry.getKey();
//            System.out.println("🧸Date: " + date);
            Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>> timeSlots = entry.getValue();
//            System.out.println("timeSlots value: " + timeSlots);

            //temp
            Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>> newTimeSlots = new LinkedHashMap<>();

            LocalDate currentDate = LocalDate.parse(date);

            dailySlotCount.putIfAbsent(currentDate, 0);

            int slotCount = dailySlotCount.get(currentDate) + 1;

            for(Map.Entry<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>> slotEntry : timeSlots.entrySet()) {

                // get time
                Pair<LocalTime, LocalTime> slot = slotEntry.getKey();
                LocalTime endSlotTime = slot.b;
//                System.out.println("slot: " + slot);

                newTimeSlots.put(slotEntry.getKey(), slotEntry.getValue());
//                System.out.println("🌻 newTimeSlots: " + newTimeSlots);

                if (slotCount % 3 == 0) {

                    LocalTime breakStart = endSlotTime;
                    LocalTime breakEnd = breakStart.plusMinutes(TIME_BREAK);

                    Pair<LocalTime, LocalTime> breakTimeSlot = new Pair<>(breakStart, breakEnd);


                    List<GetProposalScheduleDTO> breakList = new ArrayList<>();
                    breakList.add(new GetProposalScheduleDTO(
                            "breakId",
                            breakStart,
                            breakEnd,
                            date,
                            "Break",
                            "breakProjectId",
                            "noProgram",
                            "noSemester",
                            "---",
                            new ArrayList<>(),
                            "noRoom"
                    ));

                    newTimeSlots.put(breakTimeSlot, breakList);
//                    System.out.println("❗️ newTimeSlots: " + newTimeSlots);
                }
                slotCount++;

            }
            result.put(date, newTimeSlots);
            dailySlotCount.put(currentDate, slotCount);
        }

        return result;
    }
}
