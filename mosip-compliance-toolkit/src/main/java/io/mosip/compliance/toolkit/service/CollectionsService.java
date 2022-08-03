package io.mosip.compliance.toolkit.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.compliance.toolkit.config.LoggerConfiguration;
import io.mosip.compliance.toolkit.constants.AppConstants;
import io.mosip.compliance.toolkit.constants.ToolkitErrorCodes;
import io.mosip.compliance.toolkit.dto.CollectionDto;
import io.mosip.compliance.toolkit.dto.CollectionTestcasesResponseDto;
import io.mosip.compliance.toolkit.dto.CollectionsResponseDto;
import io.mosip.compliance.toolkit.dto.testcases.TestCaseDto;
import io.mosip.compliance.toolkit.entity.CollectionEntity;
import io.mosip.compliance.toolkit.entity.CollectionSummaryEntity;
import io.mosip.compliance.toolkit.repository.CollectionTestcaseRepository;
import io.mosip.compliance.toolkit.repository.CollectionsRepository;
import io.mosip.compliance.toolkit.util.ObjectMapperConfig;
import io.mosip.kernel.core.authmanager.authadapter.model.AuthUserDetails;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;

@Component
public class CollectionsService {

	@Value("${mosip.toolkit.api.id.collections.get}")
	private String getCollectionsId;

	@Value("${mosip.toolkit.api.id.collection.testcases.get}")
	private String getTestCasesForCollectionId;

	@Value("${mosip.toolkit.api.id.collection.get}")
	private String getCollectionId;

	@Autowired
	private CollectionsRepository collectionsRepository;

	@Autowired
	private CollectionTestcaseRepository collectionTestcaseRepository;

	private Logger log = LoggerConfiguration.logConfig(ProjectsService.class);

	private AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	private String getPartnerId() {
		String partnerId = authUserDetails().getUsername();
		return partnerId;
	}

	public ResponseWrapper<CollectionDto> getCollectionById(String collectionId) {
		ResponseWrapper<CollectionDto> responseWrapper = new ResponseWrapper<>();
		CollectionDto collection = null;
		try {
			CollectionEntity collectionEntity = collectionsRepository.getCollectionById(collectionId, getPartnerId());

			if (Objects.nonNull(collectionEntity)) {
				String projectId = null;
				if (Objects.nonNull(collectionEntity.getSbiProjectId())) {
					projectId = collectionEntity.getSbiProjectId();
				} else if (Objects.nonNull(collectionEntity.getSdkProjectId())) {
					projectId = collectionEntity.getSdkProjectId();
				} else if (Objects.nonNull(collectionEntity.getAbisProjectId())) {
					projectId = collectionEntity.getAbisProjectId();
				}
				ObjectMapper mapper = new ObjectMapper();
		        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
		        mapper.registerModule(new JavaTimeModule());
				collection = mapper.convertValue(collectionEntity, CollectionDto.class);
				collection.setCollectionId(collectionEntity.getId());
				collection.setProjectId(projectId);
			} else {
				List<ServiceError> serviceErrorsList = new ArrayList<>();
				ServiceError serviceError = new ServiceError();
				serviceError.setErrorCode(ToolkitErrorCodes.COLLECTION_NOT_AVAILABLE.getErrorCode());
				serviceError.setMessage(ToolkitErrorCodes.COLLECTION_NOT_AVAILABLE.getErrorMessage());				serviceErrorsList.add(serviceError);
				responseWrapper.setErrors(serviceErrorsList);
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ex.getStackTrace());
			log.error("sessionId", "idType", "id",
					"In getCollectionByCollectionId method of CollectionsService Service - " + ex.getMessage());
			List<ServiceError> serviceErrorsList = new ArrayList<>();
			ServiceError serviceError = new ServiceError();
			serviceError.setErrorCode(ToolkitErrorCodes.COLLECTION_NOT_AVAILABLE.getErrorCode());
			serviceError
					.setMessage(ToolkitErrorCodes.COLLECTION_NOT_AVAILABLE.getErrorMessage() + " " + ex.getMessage());
			serviceErrorsList.add(serviceError);
			responseWrapper.setErrors(serviceErrorsList);
		}
		responseWrapper.setId(getCollectionId);
		responseWrapper.setVersion(AppConstants.VERSION);
		responseWrapper.setResponse(collection);
		responseWrapper.setResponsetime(LocalDateTime.now());
		return responseWrapper;
	}

	public ResponseWrapper<CollectionTestcasesResponseDto> getTestcasesForCollection(String collectionId) {
		ResponseWrapper<CollectionTestcasesResponseDto> responseWrapper = new ResponseWrapper<>();
		CollectionTestcasesResponseDto collectionTestcasesResponseDto = null;

		try {
			if (Objects.nonNull(collectionId)) {
				List<String> testcases = collectionTestcaseRepository.getTestcasesByCollectionId(collectionId,
						getPartnerId());

				if (Objects.nonNull(testcases) && !testcases.isEmpty()) {
					List<TestCaseDto> collectionTestcases = new ArrayList<>(testcases.size());
					ObjectMapper mapper = new ObjectMapper();
			        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
			        mapper.registerModule(new JavaTimeModule());
					for (String testcase : testcases) {
						collectionTestcases
								.add(mapper.readValue(testcase, TestCaseDto.class));
					}
					collectionTestcasesResponseDto = new CollectionTestcasesResponseDto();
					collectionTestcasesResponseDto.setCollectionId(collectionId);
					collectionTestcasesResponseDto.setTestcases(collectionTestcases);
				} else {
					List<ServiceError> serviceErrorsList = new ArrayList<>();
					ServiceError serviceError = new ServiceError();
					serviceError.setErrorCode(ToolkitErrorCodes.INVALID_REQUEST_PARAM.getErrorCode());
					serviceError.setMessage(ToolkitErrorCodes.INVALID_REQUEST_PARAM.getErrorMessage());
					serviceErrorsList.add(serviceError);
					responseWrapper.setErrors(serviceErrorsList);
				}
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ex.getStackTrace());
			log.error("sessionId", "idType", "id",
					"In getCollectionTestcases method of CollectionsService Service - " + ex.getMessage());
			List<ServiceError> serviceErrorsList = new ArrayList<>();
			ServiceError serviceError = new ServiceError();
			serviceError.setErrorCode(ToolkitErrorCodes.COLLECTION_TESTCASES_NOT_AVAILABLE.getErrorCode());
			serviceError.setMessage(
					ToolkitErrorCodes.COLLECTION_TESTCASES_NOT_AVAILABLE.getErrorMessage() + " " + ex.getMessage());
			serviceErrorsList.add(serviceError);
			responseWrapper.setErrors(serviceErrorsList);
		}
		responseWrapper.setId(getTestCasesForCollectionId);
		responseWrapper.setVersion(AppConstants.VERSION);
		responseWrapper.setResponse(collectionTestcasesResponseDto);
		responseWrapper.setResponsetime(LocalDateTime.now());
		return responseWrapper;
	}

	public ResponseWrapper<CollectionsResponseDto> getCollections(String projectType, String projectId) {
		ResponseWrapper<CollectionsResponseDto> responseWrapper = new ResponseWrapper<>();
		CollectionsResponseDto collectionsResponse = null;
		boolean isProjectTypeValid = false;

		try {
			if (Objects.nonNull(projectType) && (AppConstants.SBI.equalsIgnoreCase(projectType)
					|| AppConstants.ABIS.equalsIgnoreCase(projectType) || AppConstants.SDK.equalsIgnoreCase(projectType))) {
				isProjectTypeValid = true;
			}

			if (isProjectTypeValid) {
				if (Objects.nonNull(projectType) && Objects.nonNull(projectId)) {
					List<CollectionSummaryEntity> collectionsEntityList = null;
					if (AppConstants.SBI.equalsIgnoreCase(projectType)) {
						collectionsEntityList = collectionsRepository.getCollectionsOfSbiProjects(projectId,
								getPartnerId());
					} else if (AppConstants.SDK.equalsIgnoreCase(projectType)) {
						collectionsEntityList = collectionsRepository.getCollectionsOfSdkProjects(projectId,
								getPartnerId());
					} else if (AppConstants.ABIS.equalsIgnoreCase(projectType)) {
						collectionsEntityList = collectionsRepository.getCollectionsOfAbisProjects(projectId,
								getPartnerId());
					}
					List<CollectionDto> collectionsList = new ArrayList<>();
					if (Objects.nonNull(collectionsEntityList) && !collectionsEntityList.isEmpty()) {
						ObjectMapper mapper = new ObjectMapper();
				        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
				        mapper.registerModule(new JavaTimeModule());
						for (CollectionSummaryEntity entity : collectionsEntityList) {
							CollectionDto collection = mapper.convertValue(entity,
									CollectionDto.class);

							collectionsList.add(collection);
						}
					} 
					// send empty collections list, in case none are yet added for a project
					collectionsResponse = new CollectionsResponseDto();
					collectionsResponse.setCollections(collectionsList);
				} else {
					List<ServiceError> serviceErrorsList = new ArrayList<>();
					ServiceError serviceError = new ServiceError();
					serviceError.setErrorCode(ToolkitErrorCodes.INVALID_REQUEST_PARAM.getErrorCode());
					serviceError.setMessage(ToolkitErrorCodes.INVALID_REQUEST_PARAM.getErrorMessage());
					serviceErrorsList.add(serviceError);
					responseWrapper.setErrors(serviceErrorsList);
				}
			} else {
				List<ServiceError> serviceErrorsList = new ArrayList<>();
				ServiceError serviceError = new ServiceError();
				serviceError.setErrorCode(ToolkitErrorCodes.INVALID_PROJECT_TYPE.getErrorCode());
				serviceError.setMessage(ToolkitErrorCodes.INVALID_PROJECT_TYPE.getErrorMessage());
				serviceErrorsList.add(serviceError);
				responseWrapper.setErrors(serviceErrorsList);
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ex.getStackTrace());
			log.error("sessionId", "idType", "id",
					"In getProjectCollectionTestrun method of CollectionsService Service - " + ex.getMessage());
			List<ServiceError> serviceErrorsList = new ArrayList<>();
			ServiceError serviceError = new ServiceError();
			serviceError.setErrorCode(ToolkitErrorCodes.COLLECTION_NOT_AVAILABLE.getErrorCode());
			serviceError
					.setMessage(ToolkitErrorCodes.COLLECTION_NOT_AVAILABLE.getErrorMessage() + " " + ex.getMessage());
			serviceErrorsList.add(serviceError);
			responseWrapper.setErrors(serviceErrorsList);
		}
		responseWrapper.setId(getCollectionsId);
		responseWrapper.setVersion(AppConstants.VERSION);
		responseWrapper.setResponse(collectionsResponse);
		responseWrapper.setResponsetime(LocalDateTime.now());
		return responseWrapper;
	}

}