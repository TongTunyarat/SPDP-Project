//package com.example.project.controller.ManageSchedule;
//
//import com.example.project.DTO.ManageSchedule.BookingSettingsDTO;
//import com.example.project.DTO.ManageSchedule.GetProposalScheduleDTO;
//import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
//import com.example.project.DTO.ManageSchedule.Preview.StudentDataDTO;
//import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
//import com.example.project.DTO.ManageSchedule.ScheduleProposalResponseDTO;
//import com.example.project.service.ManageSchedule.ManageProposalScheduleService;
//import com.example.project.service.ManageSchedule.ProposalScheduleService;
//import org.antlr.v4.runtime.misc.Pair;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.Mockito.*;
//
//class ProposalScheduleControllerTest {
//    @Mock
//    ProposalScheduleService proposalScheduleService;
//    @Mock
//    ManageProposalScheduleService manageProposalScheduleService;
//    @InjectMocks
//    ProposalScheduleController proposalScheduleController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testManageProposalSchedule() {
//        String result = proposalScheduleController.manageProposalSchedule(null, null);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetRoomSetting() {
//        when(proposalScheduleService.getRoom()).thenReturn(List.of("getRoomResponse"));
//
//        List<String> result = proposalScheduleController.getRoomSetting();
//        Assertions.assertEquals(List.of("replaceMeWithExpectedResult"), result);
//    }
//
//    @Test
//    void testGetRoomFloorSetting() {
//        when(proposalScheduleService.getRoomWithFloor()).thenReturn(Map.of("getRoomWithFloorResponse", "getRoomWithFloorResponse"));
//
//        Map<String, String> result = proposalScheduleController.getRoomFloorSetting();
//        Assertions.assertEquals(Map.of("replaceMeWithExpectedResult", "replaceMeWithExpectedResult"), result);
//    }
//
//    @Test
//    void testCheckExistingSchedule() {
//        when(proposalScheduleService.haveExitSchedule(anyString())).thenReturn(true);
//
//        Map<String, Boolean> result = proposalScheduleController.checkExistingSchedule("program");
//        Assertions.assertEquals(Map.of("replaceMeWithExpectedResult", Boolean.TRUE), result);
//    }
//
//    @Test
//    void testDeleteAllProposalSchedule() {
//        when(manageProposalScheduleService.deleteAllProposalSchedule(anyString())).thenReturn(true);
//
//        Map<String, Boolean> result = proposalScheduleController.deleteAllProposalSchedule("program");
//        Assertions.assertEquals(Map.of("replaceMeWithExpectedResult", Boolean.TRUE), result);
//    }
//
//    @Test
//    void testSaveBookingSettings() {
//        when(proposalScheduleService.prepareProject(anyString())).thenReturn(List.of(new ProjectWithInstructorsDTO(null, List.of("instructorUsernames"))));
//        when(proposalScheduleService.generateSchedule(anyString(), anyString(), anyString(), anyString(), any(List<String>.class), any(List<ProjectWithInstructorsDTO>.class))).thenReturn(new ScheduleProposalResponseDTO("status", "message", null, null));
//
//        ResponseEntity<ScheduleProposalResponseDTO> result = proposalScheduleController.saveBookingSettings(new BookingSettingsDTO("startDate", "endDate", "startTime", "endTime", List.of("rooms"), "program"));
//        Assertions.assertEquals(new ResponseEntity<ScheduleProposalResponseDTO>(new ScheduleProposalResponseDTO("status", "message", null, null), null, 0), result);
//    }
//
//    @Test
//    void testGetProposalSchedule() {
//        when(manageProposalScheduleService.getProposalSchedule(anyString())).thenReturn(Map.of("getProposalScheduleResponse", Map.of(new Pair<LocalTime, LocalTime>(LocalTime.of(23, 34, 40), LocalTime.of(23, 34, 40)), List.of(new GetProposalScheduleDTO("proposalScheduleId", LocalTime.of(23, 34, 40), LocalTime.of(23, 34, 40), "date", "status", "projectId", "program", "semester", "projectTitle", List.of("instructors"), "roomNumber")))));
//
//        Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> result = proposalScheduleController.getProposalSchedule("program");
//        Assertions.assertEquals(Map.of("replaceMeWithExpectedResult", Map.of(new Pair<LocalTime, LocalTime>(LocalTime.of(23, 34, 40), LocalTime.of(23, 34, 40)), List.of(new GetProposalScheduleDTO("proposalScheduleId", LocalTime.of(23, 34, 40), LocalTime.of(23, 34, 40), "date", "status", "projectId", "program", "semester", "projectTitle", List.of("instructors"), "roomNumber")))), result);
//    }
//
//    @Test
//    void testDeleteScheduleProject() {
//        when(manageProposalScheduleService.deleteProjectAutoGen(anyString())).thenReturn(true);
//
//        Map<String, Boolean> result = proposalScheduleController.deleteScheduleProject("projectId");
//        Assertions.assertEquals(Map.of("replaceMeWithExpectedResult", Boolean.TRUE), result);
//    }
//
//    @Test
//    void testGetDataPreviewSchedule() {
//        when(manageProposalScheduleService.getDataPreviewSchedule()).thenReturn(List.of(new PreviewProposalDTO("projectId", "projectTitle", List.of(new StudentDataDTO("studentId", "studentName", Byte.valueOf("00110"), "track")), "program", "semester", Map.of("instructorNames", List.of("instructorNames")), "date", "time", "room", "status", "editedByUser")));
//
//        ResponseEntity<List<PreviewProposalDTO>> result = proposalScheduleController.getDataPreviewSchedule();
//        Assertions.assertEquals(new ResponseEntity<List<PreviewProposalDTO>>(List.of(new PreviewProposalDTO("projectId", "projectTitle", List.of(new StudentDataDTO("studentId", "studentName", Byte.valueOf("00110"), "track")), "program", "semester", Map.of("instructorNames", List.of("instructorNames")), "date", "time", "room", "status", "editedByUser")), null, 0), result);
//    }
//
//    @Test
//    void testPrepareProject() {
//        when(proposalScheduleService.prepareProject(anyString())).thenReturn(List.of(new ProjectWithInstructorsDTO(null, List.of("instructorUsernames"))));
//
//        List<ProjectWithInstructorsDTO> result = proposalScheduleController.prepareProject("program");
//        Assertions.assertEquals(List.of(new ProjectWithInstructorsDTO(null, List.of("instructorUsernames"))), result);
//    }
//}
//
////Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme