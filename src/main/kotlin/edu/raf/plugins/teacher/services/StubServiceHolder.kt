package edu.raf.plugins.teacher.services

import com.intellij.openapi.components.Service
import raflms.teacherstub.api.TeacherStubService
import raflms.studentstub.api.StudentStubService
import raflms.teacherstub.config.ConfigFactory as TeacherConfigFactory
import raflms.studentstub.config.ConfigFactory as StudentConfigFactory

@Service(Service.Level.PROJECT)
class StubServiceHolder {
    val teacherService: TeacherStubService by lazy {
        TeacherStubService(TeacherConfigFactory.createConfig())
    }
    val studentService: StudentStubService by lazy {
        StudentStubService(StudentConfigFactory.createConfig())
    }
}