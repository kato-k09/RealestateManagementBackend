package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.ProjectRequest;
import com.katok09.realestate.management.repository.RealestateRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RealestateService {

  private RealestateRepository repository;

  @Autowired
  public RealestateService(RealestateRepository repository) {
    this.repository = repository;
  }

  public List<ProjectRequest> searchRealestate(ProjectRequest projectRequest) {
    List<ProjectRequest> result = new ArrayList<>();
    List<Project> project = repository.getProjects();
    List<Parcel> parcels = repository.getParcels();
    List<Building> building = repository.getBuildings();
    List<IncomeAndExpenses> incomeAndExpenses = repository.getIncomeAndExpenses();

    for (int i = 0; i < project.size(); i++) {
      ProjectRequest resultTemp = new ProjectRequest();
      resultTemp.setProject(project.get(i));
      resultTemp.setParcel(parcels.get(i));
      resultTemp.setBuilding(building.get(i));
      resultTemp.setIncomeAndExpenses(incomeAndExpenses.get(i));
      result.add(resultTemp);
    }

    return result;
  }

  @Transactional
  public void registerRealestate(ProjectRequest request) {
    repository.registerProject(request.getProject());

    request.getParcel().setProjectId(request.getProject().getId());
    request.getBuilding().setProjectId(request.getProject().getId());
    request.getIncomeAndExpenses().setProjectId(request.getProject().getId());

    repository.registerParcel(request.getParcel());
    repository.registerBuilding(request.getBuilding());
    repository.registerIncomeAndExpenses(request.getIncomeAndExpenses());
  }

  @Transactional
  public void updateRealestate(ProjectRequest request) {

    repository.updateProject(request.getProject());
    repository.updateParcel(request.getParcel());
    repository.updateBuilding(request.getBuilding());
    repository.updateIncomeAndExpenses(request.getIncomeAndExpenses());

  }

  @Transactional
  public void deleteRealestate(int projectId) {

    repository.deleteProject(projectId);
    repository.deleteParcel(projectId);
    repository.deleteBuilding(projectId);
    repository.deleteIncomeAndExpenses(projectId);
  }

}

